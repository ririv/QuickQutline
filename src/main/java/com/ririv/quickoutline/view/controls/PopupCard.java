package com.ririv.quickoutline.view.controls;

import javafx.animation.PauseTransition;
import javafx.event.Event;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Popup;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PopupCard extends Popup {
    private static final Logger logger = LoggerFactory.getLogger(PopupCard.class);

    PauseTransition delay = new javafx.animation.PauseTransition(Duration.seconds(1.5));;
    private boolean isHideAfterDelayWhenEscaped = true;


    public PopupCard(Parent parent) {

        parent.getStylesheets().add(getClass().getResource("PopupCard.css").toExternalForm());
        parent.getStyleClass().add("card");
        this.getScene().setRoot(parent);
//        this.setAutoHide(true); // 设置此会因为this获得焦点而导致点击按钮第一次无效（失去焦点）

        // 如果不设置宽高，第一出现popup是他们的值为0，导致出现位置错误
        // 已改为监听宽高，修复错误
//        this.setWidth(popupNode.getPrefWidth());
//        this.setHeight(popupNode.getPrefHeight());
        keepDelayWhenHover(parent);
    }

    // 窗口移动时，PopupCard没有跟着移动
    public void showEventHandler(Event event) {
        Node ownerNode = (Node) event.getSource();
        Bounds buttonBounds = ownerNode.localToScreen( ownerNode.getBoundsInLocal());
        this.widthProperty().addListener((observable, oldValue, newValue) -> {
            double x = buttonBounds.getCenterX() - newValue.doubleValue()/2;
            this.setX(x);
            logger.info("x: {}", x);
        });
        this.heightProperty().addListener((observable, oldValue, newValue) -> {
            double y = buttonBounds.getMinY() - newValue.doubleValue() - 5;
            this.setY(y);
            logger.info("y: {}", y);
        });

        logger.debug("buttonBounds: {}", buttonBounds);
        logger.debug("popup: w:{} h:{}", this.getWidth(), this.getHeight());
        this.show(ownerNode.getScene().getWindow());

        keepDelayWhenHover(ownerNode);

    }

    private void keepDelayWhenHover(Node node){
        if (isHideAfterDelayWhenEscaped) {
            node.addEventHandler(MouseEvent.MOUSE_EXITED, this::hideAfterDelay);
            node.addEventHandler(MouseEvent.MOUSE_ENTERED, this::stopDelayHide);
        }
    }

    private void hideAfterDelay(Event event) {
        delay.setOnFinished(event2 -> {
            this.hide();
            logger.info("popup hide");
        });
        delay.play();
        logger.info("popup hide start");
    }



    private void stopDelayHide(Event event) {
        if (delay!= null && delay.getStatus() == javafx.animation.Animation.Status.RUNNING){
            delay.stop();
            logger.info("popup hide stop");
        }
    }

    public void setHideAfterDelayWhenEscaped(boolean value){
        this.isHideAfterDelayWhenEscaped = value;
    }

    public void setHideAfterDelayWhenEscaped(boolean value, Duration duration){
        this.isHideAfterDelayWhenEscaped = value;
        setHideDelay(duration);
    }

    public void setHideDelay(Duration duration) {
        delay = new PauseTransition(duration);
    }


}
