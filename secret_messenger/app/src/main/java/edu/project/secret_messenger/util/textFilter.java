package edu.project.secret_messenger.util;

import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;

import java.util.regex.Pattern;

public class textFilter{
    // 영문만 허용
    public static InputFilter filterAlpha = new InputFilter() {
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Pattern ps = Pattern.compile("^[a-zA-Z]+$");

            if (!ps.matcher(source).matches()) {
                return "";
            }
            return null;
        }
    };

    // 영문만 허용 (숫자 포함)
    public static InputFilter filterAlphaNum = new InputFilter() {
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Pattern ps = Pattern.compile("^[a-zA-Z0-9]+$");

            if (!ps.matcher(source).matches()) {
                return "";
            }
            return null;
        }
    };

    // 한글만 허용
    public static InputFilter filterKor = new InputFilter() {
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Pattern ps = Pattern.compile("^[ㄱ-ㅎ가-힣]+$");

            if (!ps.matcher(source).matches()) {
                return "";
            }
            return null;
        }
    };


    //한글, 숫자, 영어 허용
    public static InputFilter filterKoEnNum = new InputFilter() {
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Pattern ps = Pattern.compile("^[a-zA-Z0-9ㄱ-ㅎ가-힣]+$");

            if (!ps.matcher(source).matches()) {
                return "";
            }
            return null;
        }
    };

    //한글, 숫자, 영어소문자, 띄어쓰기 허용
    public static InputFilter filterKoEnNum2 = new InputFilter() {
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            if(source.equals("")){ // for backspace
                return source;
            }
            if(source.toString().matches("[a-z0-9ㄱ-ㅎ가-힣 ]+")){
                return source;
            }
            Log.e("TAG", "특수문자 및 영문대문자는 입력하실 수 없습니다.");
            return "";
        }
    };
}
