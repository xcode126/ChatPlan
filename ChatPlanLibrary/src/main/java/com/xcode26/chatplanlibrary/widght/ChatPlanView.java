package com.xcode26.chatplanlibrary.widght;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xcode26.chatplanlibrary.R;
import com.xcode26.chatplanlibrary.adapter.FaceGVAdapter;
import com.xcode26.chatplanlibrary.adapter.FaceVPAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Author：sky on 2017/11/16 16:38
 * Email：xcode126@126.com
 * 封装的聊天面板，可在布局中直接引用
 */

public class ChatPlanView extends LinearLayout implements View.OnClickListener {

    private Context context;

    private MyEditText input;
    private Button send;
    private ViewPager mViewPager;//表情布局
    private LinearLayout mDotsLayout;//表情小圆点
    private LinearLayout chat_face_container;
    private ImageView image_face;//表情图标
    private int columns = 6;
    private int rows = 4;
    private List<View> views = new ArrayList<>();
    private List<String> staticFacesList;

    public ChatPlanView(Context context) {
        super(context);
        this.context = context;
        initStaticFaces();
        initView();
    }

    public ChatPlanView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initStaticFaces();
        initView();
    }

    /**
     * 初始化表情列表staticFacesList
     */
    private void initStaticFaces() {
        try {
            staticFacesList = new ArrayList<String>();
            String[] faces = context.getAssets().list("face/png");
            //将Assets中的表情名称转为字符串一一添加进staticFacesList¬
            for (int i = 0; i < faces.length; i++) {
                staticFacesList.add(faces[i]);
            }
            //去掉删除图片
            staticFacesList.remove("emotion_del_normal.png");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化视图
     */
    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.widght_chat_plan, this, true);
        //表情图标
        image_face = findViewById(R.id.image_face);
        //表情布局
        chat_face_container = findViewById(R.id.chat_face_container);
        mViewPager = findViewById(R.id.face_viewpager);
        //表情下小圆点
        mDotsLayout = findViewById(R.id.face_dots_container);
        input = findViewById(R.id.input_sms);
        send = findViewById(R.id.send_sms);
        InitViewPager();
        //表情按钮
        mViewPager.setOnPageChangeListener(new PageChange());
        input.setOnClickListener(this);
        image_face.setOnClickListener(this);
        send.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.input_sms) {
            if (chat_face_container.getVisibility() == View.VISIBLE) {
                chat_face_container.setVisibility(View.GONE);
            }
        } else if (view.getId() == R.id.image_face) {
            hideSoftInputView();//隐藏软键盘
            if (chat_face_container.getVisibility() == View.GONE) {
                chat_face_container.setVisibility(View.VISIBLE);
            } else {
                chat_face_container.setVisibility(View.GONE);
            }

        } else if (view.getId() == R.id.send_sms) {
            if (clickSendListener != null) {
                String message = input.getText().toString().trim();
                if (TextUtils.isEmpty(message)) {
                    return;
                }
                clickSendListener.OnClickSend(message, 1);
            }
            input.setText("");
            input.clearFocus();
        }
    }

    /**
     * 表情页改变时，dots效果也要跟着改变
     */
    class PageChange implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int arg0) {
            for (int i = 0; i < mDotsLayout.getChildCount(); i++) {
                mDotsLayout.getChildAt(i).setSelected(false);
            }
            mDotsLayout.getChildAt(arg0).setSelected(true);
        }

    }

    /**
     * 初始表情 *
     */
    private void InitViewPager() {
        // 获取页数
        for (int i = 0; i < getPagerCount(); i++) {
            views.add(viewPagerItem(i));
            LayoutParams params = new LayoutParams(16, 16);
            mDotsLayout.addView(dotsItem(i), params);
        }
        FaceVPAdapter mVpAdapter = new FaceVPAdapter(views);
        mViewPager.setAdapter(mVpAdapter);
        mDotsLayout.getChildAt(0).setSelected(true);
    }

    /**
     * 根据表情数量以及GridView设置的行数和列数计算Pager数量
     *
     * @return
     */
    private int getPagerCount() {
        int count = staticFacesList.size();
        return count % (columns * rows - 1) == 0 ? count / (columns * rows - 1)
                : count / (columns * rows - 1) + 1;
    }

    private View viewPagerItem(int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.emoji_face_gridview, null);//表情布局
        GridView gridview = layout.findViewById(R.id.chart_face_gv);
        /**
         * 注：因为每一页末尾都有一个删除图标，所以每一页的实际表情columns *　rows　－　1; 空出最后一个位置给删除图标
         * */
        List<String> subList = new ArrayList<String>();
        subList.addAll(staticFacesList
                .subList(position * (columns * rows - 1),
                        (columns * rows - 1) * (position + 1) > staticFacesList
                                .size() ? staticFacesList.size() : (columns
                                * rows - 1)
                                * (position + 1)));
        /**
         * 末尾添加删除图标
         * */
        subList.add("emotion_del_normal.png");
        FaceGVAdapter mGvAdapter = new FaceGVAdapter(subList, context);
        gridview.setAdapter(mGvAdapter);
        gridview.setNumColumns(columns);
        // 单击表情执行的操作
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    String png = ((TextView) ((LinearLayout) view).getChildAt(1)).getText().toString();
                    if (!png.contains("emotion_del_normal")) {// 如果不是删除图标
                        insert(getFace(png));
                    } else {
                        delete();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return gridview;
    }

    private ImageView dotsItem(int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.emoji_dot_image, null);
        ImageView iv = (ImageView) layout.findViewById(R.id.face_dot);
        iv.setId(position);
        return iv;
    }


    /**
     * 向输入框里添加表情
     */
    private void insert(CharSequence text) {
        int iCursorStart = Selection.getSelectionStart((input.getText()));
        int iCursorEnd = Selection.getSelectionEnd((input.getText()));
        if (iCursorStart != iCursorEnd) {
            ((Editable) input.getText()).replace(iCursorStart, iCursorEnd, "");
        }
        int iCursor = Selection.getSelectionEnd((input.getText()));
        ((Editable) input.getText()).insert(iCursor, text);
    }

    /**
     * 删除图标执行事件
     * 注：如果删除的是表情，在删除时实际删除的是tempText即图片占位的字符串，所以必需一次性删除掉tempText，才能将图片删除
     */
    private void delete() {
        if (input.getText().length() != 0) {
            int iCursorEnd = Selection.getSelectionEnd(input.getText());
            int iCursorStart = Selection.getSelectionStart(input.getText());
            if (iCursorEnd > 0) {
                if (iCursorEnd == iCursorStart) {
                    if (isDeletePng(iCursorEnd)) {
                        String st = "#[face/png/f_static_000.png]#";
                        ((Editable) input.getText()).delete(
                                iCursorEnd - st.length(), iCursorEnd);
                    } else {
                        ((Editable) input.getText()).delete(iCursorEnd - 1,
                                iCursorEnd);
                    }
                } else {
                    ((Editable) input.getText()).delete(iCursorStart,
                            iCursorEnd);
                }
            }
        }
    }

    /**
     * 判断即将删除的字符串是否是图片占位字符串tempText 如果是：则讲删除整个tempText
     **/
    private boolean isDeletePng(int cursor) {
        String st = "#[face/png/f_static_000.png]#";
        String content = input.getText().toString().substring(0, cursor);
        if (content.length() >= st.length()) {
            String checkStr = content.substring(content.length() - st.length(),
                    content.length());
            String regex = "(\\#\\[face/png/f_static_)\\d{3}(.png\\]\\#)";
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(checkStr);
            return m.matches();
        }
        return false;
    }

    private SpannableStringBuilder getFace(String png) {
        SpannableStringBuilder sb = new SpannableStringBuilder();
        try {
            /**
             * 经过测试，虽然这里tempText被替换为png显示，但是但我单击发送按钮时，获取到輸入框的内容是tempText的值而不是png
             * 所以这里对这个tempText值做特殊处理
             * 格式：#[face/png/f_static_000.png]#，以方便判斷當前圖片是哪一個
             * */
            String tempText = "#[" + png + "]#";
            sb.append(tempText);
            sb.setSpan(
                    new ImageSpan(context, BitmapFactory
                            .decodeStream(context.getAssets().open(png))), sb.length()
                            - tempText.length(), sb.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return sb;
    }

    /**
     * 隐藏键盘
     */
    public void hideSoftInputView() {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        // imm.toggleSoftInput(0, InputMethodManager.RESULT_HIDDEN);
        imm.showSoftInput(input, InputMethodManager.SHOW_FORCED);
        imm.hideSoftInputFromWindow(input.getWindowToken(), 0); // 强制隐藏键盘
    }


    /**
     * 按下发送按钮，使用接口回调数据和监听事件
     */
    public interface OnClickSendListener {
        /**
         * @param messageContent:发送的信息
         * @param flag:标记，用来做扩展使用，比如次面板不单只有表情，还可以扩展图片，拍照，位置等信息
         */
        void OnClickSend(String messageContent, int flag);
    }

    private OnClickSendListener clickSendListener;

    public void setClickSendListener(OnClickSendListener clickSendListener) {
        this.clickSendListener = clickSendListener;
    }
}
