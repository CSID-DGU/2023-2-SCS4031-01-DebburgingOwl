package com.example.logintest;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.KeyEvent;

import androidx.core.view.inputmethod.EditorInfoCompat;
import androidx.core.view.inputmethod.InputConnectionCompat;
import androidx.core.view.inputmethod.InputContentInfoCompat;
import androidx.appcompat.widget.AppCompatEditText;

public class NoCopyPasteEditText extends AppCompatEditText {
    public NoCopyPasteEditText(Context context) {
        super(context);
    }

    public NoCopyPasteEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoCopyPasteEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTextContextMenuItem(int id) {
        // 컨텍스트 메뉴 작업을 소비하고 싶으면 true 반환
        switch (id) {
            case android.R.id.cut:
            case android.R.id.copy:
            case android.R.id.paste:
                return true;
            default:
                return super.onTextContextMenuItem(id);
        }
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        InputConnection ic = super.onCreateInputConnection(outAttrs);
        EditorInfoCompat.setContentMimeTypes(outAttrs, new String[]{});

        final InputConnectionCompat.OnCommitContentListener callback = new InputConnectionCompat.OnCommitContentListener() {
            @Override
            public boolean onCommitContent(InputContentInfoCompat inputContentInfo, int flags, Bundle opts) {
                // 붙여넣기를 차단하기 위해 true 반환
                return true;
            }
        };

        return InputConnectionCompat.createWrapper(ic, outAttrs, callback);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_V && event.isCtrlPressed()) {
            // Ctrl+V 키 이벤트를 차단
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
