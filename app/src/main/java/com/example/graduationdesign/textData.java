package com.example.graduationdesign;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class textData extends Activity {
    private String ID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_data);
        Intent intent = getIntent();
        //获取布局
        LinearLayout textLayout = (LinearLayout)findViewById(R.id.textLayout);
        //定义文件名称
        String fname = "example.xlsx";
        Button fBut = (Button)findViewById(R.id.txt_file);
        TextView fView = (TextView)findViewById(R.id.txt_file_name);
        fBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(textData.this);
                builder.setTitle("请输入excel名称");

                final EditText input = new EditText(textData.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String text = input.getText().toString();
                        fView.setText(text+".xlsx");
                    }
                });

                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

        //监听打开excel
        Button excel_open = (Button) findViewById(R.id.txt_excel);
        excel_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView fName = (TextView)findViewById(R.id.txt_file_name);
                String fileName = fName.getText().toString();
                // 获取要打开的Excel文件的路径
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+"/data_excel/" + fileName);
                if (file.exists()) {
                    // 文件已存在，执行您的操作
                    Uri fileUri = FileProvider.getUriForFile(textData.this, getPackageName() + ".fileprovider", file);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(fileUri, "application/vnd.ms-excel");
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intent);
                } else {
                    // 文件不存在，弹出对话框
                    Toast.makeText(textData.this, "文件不存在，无法查看！", Toast.LENGTH_SHORT).show();
                }


            }
        });



        //iconText_icon的显示
        TextView iconText_id = findViewById(R.id.iconText_id);
        Typeface font = Typeface.createFromAsset(getAssets(), "iconfont.ttf");
        iconText_id.setTypeface(font);
        iconText_id.setText(getResources().getString(R.string.plants_id));
        //监听添加性状按钮
        Button add_properties = (Button)findViewById(R.id.add_properties);
        Drawable icon_add=getResources().getDrawable(R.drawable.icon_add);
        icon_add.setBounds(0,0,50,50);//必须设置图片的大小否则没有作用
        add_properties.setCompoundDrawables(null,null ,null ,icon_add);
        add_properties.setOnClickListener(new View.OnClickListener() //绑定注冊button单击事件
        {

            @Override
            public void onClick(View view) {
                final EditText et = new EditText(textData.this);
                new AlertDialog.Builder(textData.this).setTitle("请输入性状")
                        .setIcon(android.R.drawable.sym_def_app_icon)
                        .setView(et)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //按下确定键后的事件
//                                Toast.makeText(getApplicationContext(), et.getText().toString(),Toast.LENGTH_LONG).show();
                                //动态创建texteview和edittext
                                TextView tv = new EditText(textData.this);
                                tv.setText(et.getText().toString());
                                EditText dt = new EditText(getApplicationContext());
                                dt.setWidth(1000);
                                dt.setHint("请输入要记录的性状数据");
                                Button bt = new Button(textData.this);
                                bt.setText("删除");
                                //动态添加文本框和输入框
                                textLayout.addView(tv);
                                //设置文本框不允许修改
                                tv.setEnabled(false);
                                textLayout.addView(dt);
                                textLayout.addView(bt);
                                bt.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        //动态删除文本框和输入框
                                        textLayout.removeView(tv);
                                        textLayout.removeView(dt);
                                        textLayout.removeView(bt);
                                    }
                                });
                            }
                        }).setNegativeButton("取消",null).show();
            }

        });
        //监听个体查询
        Button txtQuery = (Button)findViewById(R.id.txt_query);
        Drawable icon_query=getResources().getDrawable(R.drawable.icon_query);
        icon_query.setBounds(0,0,50,50);//必须设置图片的大小否则没有作用
        txtQuery.setCompoundDrawables(icon_query,null ,null,null);
        txtQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(textData.this);
                builder.setTitle("请输入目标数据ID");

                final EditText input = new EditText(textData.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String id = input.getText().toString();
                        TextView fName = (TextView)findViewById(R.id.txt_file_name);
                        String fileName = fName.getText().toString();
                        ExcelManager excelManager = new ExcelManager(getApplicationContext());
                        try {
                            String str = excelManager.queryData(fileName,id);
                            if(str.equals("@")){
                                AlertDialog.Builder builder = new AlertDialog.Builder(textData.this);
                                builder.setMessage("查询失败，个体不存在！"); // 设置要显示的消息
                                builder.setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // 用户单击"OK"按钮后执行的操作
                                    }
                                });
                                AlertDialog dialog0 = builder.create();
                                dialog0.show(); // 显示对话框
                            }else{
                                String[] result = new String[2];
                                result = str.split("@");
                                AlertDialog.Builder builder = new AlertDialog.Builder(textData.this);
                                builder.setMessage("查询成功！录入数据为：\n"+result[0]+"\n"+result[1]); // 设置要显示的消息
                                builder.setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // 用户单击"OK"按钮后执行的操作
                                    }
                                });
                                AlertDialog dialog1 = builder.create();
                                dialog1.show(); // 显示对话框
                            }

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });

                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

        //监听excel数据总量
        Button txtDataSize = (Button)findViewById(R.id.txt_dataSize);
        Drawable icon_dataSize=getResources().getDrawable(R.drawable.icon_data_size);
        icon_dataSize.setBounds(0,0,50,50);//必须设置图片的大小否则没有作用
        txtDataSize.setCompoundDrawables(icon_dataSize,null ,null,null);
        txtDataSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView fName = (TextView)findViewById(R.id.txt_file_name);
                String fileName = fName.getText().toString();
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+"/data_excel/" + fileName);
                if(!file.exists()){
                    Toast.makeText(textData.this, "文件不存在，无法查看有效数据！", Toast.LENGTH_SHORT).show();
                }else{
                    ExcelManager excelManager = new ExcelManager(getApplicationContext());
                    try {
                        int excelDataSize = excelManager.getExcelDataSize(fileName);
                        AlertDialog.Builder builder = new AlertDialog.Builder(textData.this);
                        builder.setMessage(fileName+" 中有效数据有 "+excelDataSize+" 条"); // 设置要显示的消息
                        builder.setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // 用户单击"OK"按钮后执行的操作
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show(); // 显示对话框

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });


        //监听个体删除
        Button txtDelData = (Button)findViewById(R.id.txt_del_data);
        Drawable icon_delete=getResources().getDrawable(R.drawable.icon_delete);
        icon_delete.setBounds(0,0,50,50);//必须设置图片的大小否则没有作用
        txtDelData.setCompoundDrawables(icon_delete,null ,null,null);
        txtDelData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(textData.this);
                builder.setTitle("请输入目标数据ID");
                final EditText input = new EditText(textData.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String id = input.getText().toString();
                        TextView fName = (TextView)findViewById(R.id.txt_file_name);
                        String fileName = fName.getText().toString();
                        ExcelManager excelManager = new ExcelManager(getApplicationContext());
                        int index = -1;
                        try {
                            index = excelManager.deleteData(fileName,id);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        if(index==-1){
                            AlertDialog.Builder builder = new AlertDialog.Builder(textData.this);
                            builder.setMessage("查询失败，个体不存在！"); // 设置要显示的消息
                            builder.setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // 用户单击"OK"按钮后执行的操作
                                }
                            });
                            AlertDialog dialog0 = builder.create();
                            dialog0.show(); // 显示对话框
                        }else{
                            AlertDialog.Builder builder = new AlertDialog.Builder(textData.this);
                            builder.setMessage("已删除"); // 设置要显示的消息
                            builder.setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // 用户单击"OK"按钮后执行的操作
                                }
                            });
                            AlertDialog dialog0 = builder.create();
                            dialog0.show(); // 显示对话框
                        }
                    }
                });

                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });



        //监听form按钮
        Button form = (Button)findViewById(R.id.form);
        Drawable icon_submit=getResources().getDrawable(R.drawable.icon_submit);
        icon_submit.setBounds(0,0,60,60);//必须设置图片的大小否则没有作用
        form.setCompoundDrawables(null,null ,icon_submit ,null);
        form.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText onlyId=(EditText) findViewById(R.id.only_id);
                ID = onlyId.getText().toString();
                String message = "";
                //创建集合对象
                LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
                //遍历linearLayout
                for (int i = 6; i < textLayout.getChildCount(); i+=3) {
                    //获取一个线性布局的view
                    TextView textview = (TextView) textLayout.getChildAt(i);
                    EditText edittext  = (EditText) textLayout.getChildAt(i+1);
                    String context = textview.getText().toString();
                    String dat = edittext.getText().toString();
                    //添加数据到集合中
                    map.put(context,dat);
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(textData.this);
                builder.setTitle("是否确认提交数据");
                if(!"".equals(ID)){
                    message += "ID: "+ID+"\n"+"数据⬇: \n";
                    // 获取所有键值对对象的集合
                    Set<Map.Entry<String, String>> set = map.entrySet();
                    for (Map.Entry<String, String> me : set) {
                        // 根据键值对对象获取键和值
                        String key = me.getKey();
                        String value = me.getValue();
                        message += key+": "+value+"\n";
                    }
                    builder.setMessage(message);
                    builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ExcelManager excelManager = new ExcelManager(getApplicationContext());
                            TextView fName = (TextView)findViewById(R.id.txt_file_name);
                            String fileName = fName.getText().toString();
                            excelManager.createExcelFile(fileName,map,ID);
                            Toast.makeText(getApplicationContext(), "ID为"+ID+"数据已提交",Toast.LENGTH_LONG).show();
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                }
                else{
                    Toast.makeText(getApplicationContext(), "请输入ID后，再重新提交数据",Toast.LENGTH_LONG).show();
                }

            }
        });
        //已提交性状iconText_list
        TextView iconText_list = findViewById(R.id.iconText_list);
        iconText_list.setTypeface(font);
        iconText_list.setText(getResources().getString(R.string.plants_flag));


    }





    //解决屏幕旋转导致动态组件和数据消失
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //获取布局
        LinearLayout textLayout = (LinearLayout)findViewById(R.id.textLayout);
        //遍历linearLayout
        for (int i = 6; i < textLayout.getChildCount(); i+=3) {
            //获取一个线性布局的view
            TextView textview = (TextView) textLayout.getChildAt(i);
            EditText edittext  = (EditText) textLayout.getChildAt(i+1);
            outState.putString("text"+i, textview.getText().toString());
            outState.putString("edit"+i, edittext.getText().toString());
        }
        outState.putInt("index", textLayout.getChildCount());
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //获取布局
        LinearLayout textLayout = (LinearLayout)findViewById(R.id.textLayout);
        int index = savedInstanceState.getInt("index");
        // 恢复组件状态
        for (int i = 5; i < index; i += 3) {
            String text = savedInstanceState.getString("text"+i);
            String edit = savedInstanceState.getString("edit"+i);
            TextView tv = new EditText(textData.this);
            tv.setText(text);
            textLayout.addView(tv);
            tv.setEnabled(false);
            TextView dt = new EditText(textData.this);
            dt.setText(edit);
            textLayout.addView(dt);
            Button bt = new Button(textData.this);
            bt.setText("删除");
            textLayout.addView(bt);
        }
    }






}