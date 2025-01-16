package com.ririv.quickoutline.view;

import com.ririv.quickoutline.service.TocService;
import com.ririv.quickoutline.view.controls.Message;
import com.ririv.quickoutline.view.controls.Switch;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class GetContentsPopupView extends StackPane {

    private StringProperty filepath = new SimpleStringProperty();

    public HBox pageNumRangeLayout;

    private MainController mainController;

    @FXML
    private Switch autoRecognizeSwitch;

    @FXML
    private TextField startTF;

    @FXML
    private TextField endTF;

    @FXML
    private Button extractTocBtn;

    private int backspaceCount = 0;

    private BooleanProperty autoRecognize = new SimpleBooleanProperty(true);

    public GetContentsPopupView(MainController mainController) {
        this.mainController = mainController;

        // 通过 FXMLLoader 加载 FXML
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("GetContentsPopup.fxml"));
        fxmlLoader.setRoot(this); // 设置根节点为当前 Message 实例
        fxmlLoader.setController(this); // 设置控制器为当前 Message 实例
        try {
            fxmlLoader.load(); // 加载 FXML 布局
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initialize() {
        TocService tocService = new TocService();


        autoRecognize.bind(autoRecognizeSwitch.valueProperty());


        pageNumRangeLayout.disableProperty().bind(autoRecognizeSwitch.valueProperty());


        startTF.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            if (!"0123456789".contains(event.getCharacter())) {
                event.consume();
            }
        });

        endTF.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            if (!"0123456789".contains(event.getCharacter())) {
                event.consume();
            }
        });

        // 启用tab键自动转到endTF
        endTF.focusTraversableProperty().bind(startTF.focusedProperty());

        endTF.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (endTF.getText().isEmpty()){
                if (event.getCode() == KeyCode.BACK_SPACE) {
                    backspaceCount++;
                    if (backspaceCount == 2) {
                        // 当退格键被按下两次时触发的事件
                        startTF.requestFocus();
                        // 重置计数器
                        backspaceCount = 0;
                    }
                }
            }

        });

        extractTocBtn.setOnAction(event -> {
            if (filepath.get() == null || filepath.get().isEmpty()) {
                mainController.messageDialog.showMessage("请选择PDF文件", Message.MessageType.WARNING);
                return;
            }

            String contents = "";
            if (autoRecognize.get()) {
                contents = tocService.extract(filepath.get());
            } else {
                if (startTF.getText().isEmpty() || endTF.getText().isEmpty()) {
                    mainController.messageDialog.showMessage("请输入起始页码和结束页码", Message.MessageType.WARNING);
                }
                contents = tocService.extract(filepath.get(), Integer.parseInt(startTF.getText()), Integer.parseInt(endTF.getText()));
            }
            this.mainController.textModeController.contentsTextArea.setText(contents);
        });
    }

    public StringProperty filepathProperty() {
        return filepath;
    }

}