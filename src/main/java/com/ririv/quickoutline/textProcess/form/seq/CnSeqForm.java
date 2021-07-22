package com.ririv.quickoutline.textProcess.form.seq;

import com.ririv.quickoutline.exception.BookmarkFormatException;
import com.ririv.quickoutline.entity.Bookmark;
import com.ririv.quickoutline.textProcess.form.Form;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.ririv.quickoutline.textProcess.PreProcess.OneBlank;
import static com.ririv.quickoutline.textProcess.PreProcess.TwoBlank;


public class CnSeqForm extends Form implements StandardSeqForm {
    final Pattern cnPattern = Pattern.compile(
            "^(\\s*)?"  //缩进$1
                    + "(\\S?\\s?[零一二三四五六七八九十百千0-9]+\\s?(篇|章|节|部分)|[0-9.]+)?"  //序号$2   $3不用
                    + "\\s*"
                    + "(.*?)" //标题$4
                    + "[\\s.]*"
                    + "(-?[0-9]+)?" //页码$5
                    + "\\s*$");


    private String secondPreprocess(String line, Matcher matcher) {
        return null;
    }


    public Bookmark addBookmarkByLine(int offset, Bookmark last, String line, int index) {
        Matcher matcher = cnPattern.matcher(line);
        if (matcher.find()) {
            String lnIndent = matcher.group(1); //行缩进
            String rawSeq = matcher.group(2) != null ? matcher.group(2) : ""; //原seq字符串
            rawSeq = rawSeq.replaceAll(OneBlank, "");
            String seq = checkSeq(rawSeq); //检测到的seq
            String title = (rawSeq + TwoBlank + matcher.group(4)).trim();
            Integer pageNum;

            if (matcher.group(5) != null) { //页码
                pageNum = Integer.parseInt(matcher.group(5)) + offset;
            } else { //页码为空
                pageNum = null;
            }

            int level = checkLevelBySeq(seq);
            Bookmark current = new Bookmark(title, pageNum);
            current.setIndex(index);
            current.setSeq(seq);
            last = addBookmarkByLevel(current, last, level);

        } else {
            throw new BookmarkFormatException(String.format(
                    "添加页码错误\n\"%s\"格式不正确",
                    line), index);
        }
        return last;
    }

    //下面两个先使用standard的，以后再写更具体的，以兼容更多不同格式的目录
//    public String checkSeq(String rawSeq) {
//        return null;
//    }
//
//    public int checkLevelBySeq(String seq) {
//
//        return 0;
//    }

    public void postProcess(Bookmark root) {
        boolean isLocated = false;
        Bookmark mark = null;
        for (Bookmark current : root.getChildren()) {
            if (current.getTitle().matches("^第[零一二三四五六七八九十百千0-9]部分.*")) {
                isLocated = true;
                mark = current;
            } else if (current.getTitle().matches("^(附录).*")) {
                isLocated = false;
            } else if (isLocated) {
                current.changePos(mark);
            }
        }
    }

}