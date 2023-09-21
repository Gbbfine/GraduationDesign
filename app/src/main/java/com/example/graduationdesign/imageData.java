package com.example.graduationdesign;

import androidx.annotation.Nullable;
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
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class imageData extends Activity {
    //相机事件
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    //相册事件
    private static final int REQUEST_CODE_SELECT_IMAGE = 2;
    //同时获取相机相册权限
    private static final int MY_REQUEST_CODE = 3000;
    //照片存储文件夹
    private static String mDir = "";
    //照片存储路径
    private String mCurrentPhotoPath = "";
    //照片名称
    private String mCurrentPhotoName = "";
    //植株id
    private String ID = "";
    private File documentsDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
    private File imgDirectory = new File(documentsDirectory, "data_img");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_data);
        //同时请求相机、读写、相册权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_REQUEST_CODE);
        }
        Intent intent =getIntent();
        //获取布局
        LinearLayout imageLayout = (LinearLayout)findViewById(R.id.imageLayout);
        //定义文件名称
        String fname = "example_image.xlsx";
        Button fBut = (Button)findViewById(R.id.img_file);
        TextView fView = (TextView)findViewById(R.id.img_file_name);
        fBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(imageData.this);
                builder.setTitle("请输入excel名称");

                final EditText input = new EditText(imageData.this);
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
        Button excel_open = (Button) findViewById(R.id.img_excel);
        excel_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView fName = (TextView)findViewById(R.id.img_file_name);
                String fileName = fName.getText().toString();
                // 获取要打开的Excel文件的路径
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+"/data_excel/" + fileName);
                if (file.exists()) {
                    // 文件已存在，执行您的操作
                    Uri fileUri = FileProvider.getUriForFile(imageData.this, getPackageName() + ".fileprovider", file);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(fileUri, "application/vnd.ms-excel");
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(intent);
                } else {
                    // 文件不存在，弹出对话框
                    Toast.makeText(imageData.this, "文件不存在，无法查看！", Toast.LENGTH_SHORT).show();
                }


            }
        });

        //iconImg_icon的显示
        Typeface font = Typeface.createFromAsset(getAssets(), "iconfont.ttf");
        TextView iconImg_id = findViewById(R.id.iconImg_id);
        iconImg_id.setTypeface(font);
        iconImg_id.setText(getResources().getString(R.string.plants_id));
        //监听照片存储按钮
        Button picDir = (Button)findViewById(R.id.pic_dir);
        Drawable icon_dir=getResources().getDrawable(R.drawable.icon_dir);
        icon_dir.setBounds(0,0,50,50);//必须设置图片的大小否则没有作用
        picDir.setCompoundDrawables(icon_dir,null ,null,null);//设置图片left这里如果是右边就放到第二个参数里面依次对应
        picDir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(imageData.this);
                builder.setTitle("请输入文件夹名称");

                final EditText input = new EditText(imageData.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDir = input.getText().toString();
                        TextView DirView = (TextView)findViewById(R.id.dir_name);
                        DirView.setText("/"+mDir);
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
        //监听跳转图像存储文件夹
        Button imgDir_open = (Button) findViewById(R.id.img_dir);
        Drawable icon_see=getResources().getDrawable(R.drawable.icon_see);
        icon_see.setBounds(0,0,50,50);//必须设置图片的大小否则没有作用
        imgDir_open.setCompoundDrawables(icon_see,null ,null,null);
        imgDir_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView dirName = (TextView)findViewById(R.id.dir_name);
                String fileName = dirName.getText().toString();
                // 获取要打开的Excel文件的路径
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+"/data_img" + fileName);
                if (file.exists()) {
                    // 文件已存在，执行您的操作
                    Uri uri = DocumentsContract.buildDocumentUri(
                            "com.android.externalstorage.documents",
                            "primary:" + Environment.DIRECTORY_DOCUMENTS + "/data_img" + fileName
                    );

                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("*/*");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri);
                    startActivity(intent);
                } else {
                    // 文件不存在，弹出对话框
                    Toast.makeText(imageData.this, "文件不存在，无法查看！", Toast.LENGTH_SHORT).show();
                }


            }
        });
        //监听图像数量
        Button imgNum = (Button)findViewById(R.id.img_num);
        Drawable icon_imgNum=getResources().getDrawable(R.drawable.icon_img_num);
        icon_imgNum.setBounds(0,0,50,50);//必须设置图片的大小否则没有作用
        imgNum.setCompoundDrawables(icon_imgNum,null ,null,null);
        imgNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView dirName = (TextView) findViewById(R.id.dir_name);
                String fileName = dirName.getText().toString();
                // 获取要打开的Excel文件的路径
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+"/data_img" + fileName);
                if(file.exists()){
                    int num = countJpegFiles(file);
                    AlertDialog.Builder builder = new AlertDialog.Builder(imageData.this);
                    builder.setMessage("."+fileName+" 目录下有效图像数据有 "+num+" 张"); // 设置要显示的消息
                    builder.setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // 用户单击"OK"按钮后执行的操作
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show(); // 显示对话框
                }else{
                    // 目录不存在，弹出对话框
                    Toast.makeText(imageData.this, "目录不存在，图像数据为0！", Toast.LENGTH_SHORT).show();
                }
            }
        });


        //调用相机拍照，监听相机拍照事件
        Button take_photo = (Button)findViewById(R.id.take_photo);
        Drawable icon_camera=getResources().getDrawable(R.drawable.icon_camera);
        icon_camera.setBounds(0,0,50,50);//必须设置图片的大小否则没有作用
        take_photo.setCompoundDrawables(icon_camera,null ,null,null);
        take_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取ID
                EditText onlyId=(EditText) findViewById(R.id.only_id);
                ID = onlyId.getText().toString();
                if("".equals(ID)){
                    Toast.makeText(getApplicationContext(), "请输入唯一ID后，再调用相机",Toast.LENGTH_LONG).show();
                }else{
                    //先删除上次未提交的图像
                    File delImg =new File(mCurrentPhotoPath);
                    if(delImg.exists()){
                        delImg.delete();
                    }
                    //调用相机拍照
                    dispatchTakePictureIntent();
                }
            }
        });
        //监听相册按钮
        Button album = (Button)findViewById(R.id.album);
        Drawable icon_album=getResources().getDrawable(R.drawable.icon_album);
        icon_album.setBounds(0,0,50,50);//必须设置图片的大小否则没有作用
        album.setCompoundDrawables(icon_album,null ,null,null);
        album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取ID
                EditText onlyId=(EditText) findViewById(R.id.only_id);
                ID = onlyId.getText().toString();
                if("".equals(ID)){
                    Toast.makeText(getApplicationContext(), "请输入唯一ID后，再使用相册",Toast.LENGTH_LONG).show();
                }else{
                    //先删除上次未提交的图像
                    File delImg =new File(mCurrentPhotoPath);
                    if(delImg.exists()){
                        delImg.delete();
                    }
                    //调用相册
                    dispatchAlbumIntent();
                }
            }
        });


        //监听删除按钮
        Button del_pic = (Button)findViewById(R.id.del_pic);
        Drawable icon_delete=getResources().getDrawable(R.drawable.icon_delete);
        icon_delete.setBounds(0,0,50,50);//必须设置图片的大小否则没有作用
        del_pic.setCompoundDrawables(icon_delete,null ,null,null);
        del_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(imageData.this);
                builder.setTitle("是否删除图像");
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        File delImg =new File(mCurrentPhotoPath);
                        if(delImg.exists()){
                            delImg.delete();
                            Toast.makeText(getApplicationContext(), "图像已删除!",Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(getApplicationContext(), "图像不存在，删除失败！",Toast.LENGTH_LONG).show();
                        }
                        mCurrentPhotoPath = "";
                        mCurrentPhotoName = "还未收集图片";
                        ImageView imgVw = (ImageView) findViewById(R.id.imageView);
                        imgVw.setImageDrawable(null);
                        TextView picNameTv = (TextView) findViewById(R.id.pic_name);
                        picNameTv.setText(mCurrentPhotoName);
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
        //监听一键生成图片名称按钮
        Button modify_simple = (Button)findViewById(R.id.modify_simple);
        modify_simple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取ID
                EditText onlyId=(EditText) findViewById(R.id.only_id);
                ID = onlyId.getText().toString();
                if(!"".equals(ID)){
                    //创建集合对象
                    LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
                    //遍历linearLayout
                    for (int i = 10; i < imageLayout.getChildCount(); i+=3) {
                        //获取一个线性布局的view
                        TextView textview = (TextView) imageLayout.getChildAt(i);
                        EditText edittext  = (EditText) imageLayout.getChildAt(i+1);
                        String context = textview.getText().toString();
                        String dat = edittext.getText().toString();
                        //添加数据到集合中
                        map.put(context,dat);
                    }
                    // 获取所有键值对对象的集合
                    Set<Map.Entry<String, String>> set = map.entrySet();
                    mCurrentPhotoName = "JPEG_ID-"+ID+"_";
                    // 遍历键值对对象的集合，得到每一个键值对对象
                    for (Map.Entry<String, String> me : set) {
                        // 根据键值对对象获取键和值
                        String key = me.getKey();
                        String value = me.getValue();
                        mCurrentPhotoName += key+"-"+value+"_";
                        Log.v("输出","");
                        Log.v(key ,value);
                    }
                    // 创建一个以当前时间命名的图片文件
//                    String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                    long timestamp = System.currentTimeMillis();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                    String timeStamp = dateFormat.format(new Date(timestamp));
                    mCurrentPhotoName += "time-"+timeStamp+".jpg";
                    TextView picNameTv = (TextView) findViewById(R.id.pic_name);
                    picNameTv.setText(mCurrentPhotoName);
                }else{
                    Toast.makeText(getApplicationContext(), "请输入唯一ID后，再重新生成名称",Toast.LENGTH_LONG).show();
                }

            }
        });
        //监听自定义图片名称按钮
        Button modify_complex = (Button)findViewById(R.id.modify_complex);
        modify_complex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(imageData.this);
                builder.setTitle("请输入图片名称");

                final EditText input = new EditText(imageData.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String str = input.getText().toString();
                        // 定义正则表达式，包含所有非法字符
                        String illegalRegex = "[\\\\/:*?\"<>| ]";
                        // 判断字符串中是否包含非法字符
                        Pattern pattern = Pattern.compile(illegalRegex);
                        Matcher matcher = pattern.matcher(str);
                        if(matcher.find()){
                            Toast.makeText(getApplicationContext(), "图像名称含有非法字符，请重新输入！",Toast.LENGTH_LONG).show();
                        }else{
                            mCurrentPhotoName = str+".jpg";
                            TextView picNameTv = (TextView) findViewById(R.id.pic_name);
                            picNameTv.setText(mCurrentPhotoName);
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

        //监听添加属性按钮
        Button add_properties = (Button)findViewById(R.id.add_properties);
        Drawable icon_add=getResources().getDrawable(R.drawable.icon_add);
        icon_add.setBounds(0,0,50,50);//必须设置图片的大小否则没有作用
        add_properties.setCompoundDrawables(null,null ,null ,icon_add);
        add_properties.setOnClickListener(new View.OnClickListener() //绑定注冊button单击事件
        {
            @Override
            public void onClick(View view) {

                final EditText et = new EditText(imageData.this);
                new AlertDialog.Builder(imageData.this).setTitle("请输入性状")
                        .setIcon(android.R.drawable.sym_def_app_icon)
                        .setView(et)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //按下确定键后的事件
//                                Toast.makeText(getApplicationContext(), et.getText().toString(),Toast.LENGTH_LONG).show();
                                //动态创建texteview和edittext
                                TextView tv = new EditText(imageData.this);
                                tv.setText(et.getText().toString());
                                EditText dt = new EditText(getApplicationContext());
                                dt.setWidth(1000);
                                dt.setHint("请输入要记录的性状数据");
                                Button bt = new Button(imageData.this);
                                bt.setText("删除");
                                //动态添加文本框和输入框
                                imageLayout.addView(tv);
                                //设置文本框不允许修改
                                tv.setEnabled(false);
                                imageLayout.addView(dt);
                                imageLayout.addView(bt);
                                bt.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        //动态删除文本框和输入框
                                        imageLayout.removeView(tv);
                                        imageLayout.removeView(dt);
                                        imageLayout.removeView(bt);
                                    }
                                });
                            }
                        }).setNegativeButton("取消",null).show();
            }

        });

        //监听个体查询
        Button imgQuery = (Button)findViewById(R.id.img_query);
        Drawable icon_query=getResources().getDrawable(R.drawable.icon_query);
        icon_query.setBounds(0,0,50,50);//必须设置图片的大小否则没有作用
        imgQuery.setCompoundDrawables(icon_query,null ,null,null);
        imgQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(imageData.this);
                builder.setTitle("请输入目标数据ID");

                final EditText input = new EditText(imageData.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String id = input.getText().toString();
                        TextView fName = (TextView)findViewById(R.id.img_file_name);
                        String fileName = fName.getText().toString();
                        ExcelManager excelManager = new ExcelManager(getApplicationContext());
                        try {
                            String str = excelManager.queryData(fileName,id);
                            if(str.equals("@")){
                                AlertDialog.Builder builder = new AlertDialog.Builder(imageData.this);
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
                                AlertDialog.Builder builder = new AlertDialog.Builder(imageData.this);
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
        Button imgDataSize = (Button)findViewById(R.id.img_dataSize);
        Drawable icon_dataSize=getResources().getDrawable(R.drawable.icon_data_size);
        icon_dataSize.setBounds(0,0,50,50);//必须设置图片的大小否则没有作用
        imgDataSize.setCompoundDrawables(icon_dataSize,null ,null,null);
        imgDataSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView fName = (TextView)findViewById(R.id.img_file_name);
                String fileName = fName.getText().toString();
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+"/data_excel/" + fileName);
                if(!file.exists()){
                    Toast.makeText(imageData.this, "文件不存在，无法查看有效数据！", Toast.LENGTH_SHORT).show();
                }else{
                    ExcelManager excelManager = new ExcelManager(getApplicationContext());
                    try {
                        int excelDataSize = excelManager.getExcelDataSize(fileName);
                        AlertDialog.Builder builder = new AlertDialog.Builder(imageData.this);
                        builder.setMessage(fileName+" 中有效数据有 "+excelDataSize+" 条"); // 设置要显示的消息
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
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
        Button imgDelData = (Button)findViewById(R.id.img_del_data);
        imgDelData.setCompoundDrawables(icon_delete,null ,null,null);
        imgDelData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(imageData.this);
                builder.setTitle("请输入目标数据ID");
                final EditText input = new EditText(imageData.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String id = input.getText().toString();
                        TextView fName = (TextView)findViewById(R.id.img_file_name);
                        String fileName = fName.getText().toString();
                        ExcelManager excelManager = new ExcelManager(getApplicationContext());
                        int index = -1;
                        try {
                            index = excelManager.deleteData(fileName,id);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        if(index==-1){
                            AlertDialog.Builder builder = new AlertDialog.Builder(imageData.this);
                            builder.setMessage("查询失败，个体不存在！"); // 设置要显示的消息
                            builder.setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // 用户单击"OK"按钮后执行的操作
                                }
                            });
                            AlertDialog dialog0 = builder.create();
                            dialog0.show(); // 显示对话框
                        }else{
                            AlertDialog.Builder builder = new AlertDialog.Builder(imageData.this);
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
                if(!"".equals(ID)){
                    String message = "";
                    message+="ID: "+ID+"\n"+"数据⬇: \n";
                    //创建集合对象
                    LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
                    //遍历linearLayout
                    for (int i = 10; i < imageLayout.getChildCount(); i+=3) {
                        //获取一个线性布局的view
                        TextView textview = (TextView) imageLayout.getChildAt(i);
                        EditText edittext  = (EditText) imageLayout.getChildAt(i+1);
                        String context = textview.getText().toString();
                        String dat = edittext.getText().toString();
                        //添加数据到集合中
                        map.put(context,dat);
                        message+=context+": "+dat+"\n";
                    }
                    //如果图像不为空
                    if(mCurrentPhotoPath!=""){
                        //记录图像名称
                        map.put("picture_name",mCurrentPhotoName);
                        //修改图片名称为最新图像
                        File oldFile = new File(mCurrentPhotoPath);
                        if(mDir.equals("")){
                            mCurrentPhotoPath = imgDirectory+"/"+mCurrentPhotoName;
                        }else{
//                            File storageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES)+"/"+mDir);
                            File storageDir = new File(imgDirectory+"/"+mDir);
                            if (!storageDir.exists()) {
                                storageDir.mkdirs();
                            }
//                            mCurrentPhotoPath = getExternalFilesDir(Environment.DIRECTORY_PICTURES)+"/"+mDir+"/"+mCurrentPhotoName;
                            mCurrentPhotoPath = imgDirectory+"/"+mDir+"/"+mCurrentPhotoName;
                        }
                        File newFile = new File(mCurrentPhotoPath);
                        oldFile.renameTo(newFile);
                        map.put("picture_path",mCurrentPhotoPath);
                    }
                    // 获取所有键值对对象的集合
                    Set<Map.Entry<String, String>> set = map.entrySet();
                    // 遍历键值对对象的集合，得到每一个键值对对象
                    for (Map.Entry<String, String> me : set) {
                        // 根据键值对对象获取键和值
                        String key = me.getKey();
                        String value = me.getValue();
                    }
                    message+="photoName: "+mCurrentPhotoName;
                    AlertDialog.Builder builder = new AlertDialog.Builder(imageData.this);
                    builder.setTitle("是否确认提交数据");
                    builder.setMessage(message);
                    builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            ExcelManager excelManager = new ExcelManager(getApplicationContext());
                            TextView fName = (TextView)findViewById(R.id.img_file_name);
                            String fileName = fName.getText().toString();
                            //输入id之后才能提交，否则提示没有输入id
                            // 创建Excel文件
                            excelManager.createExcelFile(fileName,map,ID);
                            Toast.makeText(getApplicationContext(), "ID为"+ID+"数据已提交",Toast.LENGTH_LONG).show();
                            //提交后初始化图像参数
                            mCurrentPhotoPath = "";
                            mCurrentPhotoName = "还未收集图片";
                            ImageView imgVw = (ImageView) findViewById(R.id.imageView);
                            imgVw.setImageDrawable(null);
                            TextView picNameTv = (TextView) findViewById(R.id.pic_name);
                            picNameTv.setText(mCurrentPhotoName);
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                }else{
                    Toast.makeText(getApplicationContext(), "请输入ID后，再重新提交数据",Toast.LENGTH_LONG).show();
                }
            }
        });
        //已提交性状iconImg_list
        TextView iconImg_list = findViewById(R.id.iconImg_list);
        iconImg_list.setTypeface(font);
        iconImg_list.setText(getResources().getString(R.string.plants_flag));
    }
    //调用相机
    private void dispatchTakePictureIntent() {
        // 创建启动相机的Intent
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // 创建保存图片的File对象
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // 出现异常
                ex.printStackTrace();
            }
            if (photoFile != null) {
                // 将File对象转换为Uri对象，以便在Intent中传递
                Uri photoUri = FileProvider.getUriForFile(imageData.this,
                        "com.example.graduationdesign.fileprovider",
                        photoFile);
                // 将Uri对象添加到Intent中
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                // 启动相机
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }
    //创建图像文件
    private File createImageFile() throws IOException {
        // 创建一个以当前时间命名的图片文件
//        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        long timestamp = System.currentTimeMillis();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String timeStamp = dateFormat.format(new Date(timestamp));
        String imageFileName = "JPEG_" + timeStamp + "_.jpg";
        File storageDir;

        if("".equals(mDir)){
            storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//            storageDir = imgDirectory;
            if (!storageDir.exists()) {
                storageDir.mkdirs();
            }
        }else{
            storageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES)+"/"+mDir);
//            storageDir = new File(imgDirectory+"/"+mDir);
            if (!storageDir.exists()) {
                storageDir.mkdirs();
            }
        }
        // 创建图片文件
//        File imageFile = File.createTempFile(
//                imageFileName,  /* prefix */
//                ".jpg",         /* suffix */
//                storageDir      /* directory */
//        );
        File imageFile = new File(storageDir, imageFileName);
        try {
            imageFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 保存图片文件路径
        mCurrentPhotoPath = imageFile.getAbsolutePath();
        //保存文件名称
        mCurrentPhotoName = imageFile.getName();
        return imageFile;
    }
    //调用相册
    private void dispatchAlbumIntent(){
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
    }
    private String getImagePath(Uri imageUri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(imageUri, projection, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            String imagePath = cursor.getString(columnIndex);
            cursor.close();
            return imagePath;
        }
        return null;
    }
    private void saveImageToFile(File imageFile) {
        try {
            File storageDir;
            File tempFile = null;
            File documentsDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);

            if("".equals(mDir)){
//                storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                //公共存储
                storageDir = imgDirectory;
                if (!storageDir.exists()) {
                    storageDir.mkdirs();
                }
            }else{
//                storageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES)+"/"+mDir);
                storageDir = new File(imgDirectory+"/"+mDir);
                if (!storageDir.exists()) {
                    storageDir.mkdirs();
                }
            }
            InputStream inputStream = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                inputStream = Files.newInputStream(imageFile.toPath());
            }
            OutputStream outputStream = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                // 创建一个以当前时间命名的图片文件
//                String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
//                String imageFileName = "JPEG_" + timeStamp + "_";

                long timestamp = System.currentTimeMillis();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                String timeStamp = dateFormat.format(new Date(timestamp));
                String imageFileName = "JPEG_" + timeStamp + "_.jpg";
//                tempFile = File.createTempFile(
//                        imageFileName,  /* prefix */
//                        ".jpg",         /* suffix */
//                        storageDir      /* directory */
//                );
                tempFile = new File(storageDir, imageFileName);
                try {
                    tempFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                outputStream = Files.newOutputStream(tempFile.toPath());
            }
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.flush();
            outputStream.close();
            inputStream.close();
            // 保存图片文件路径
            mCurrentPhotoPath = tempFile.getAbsolutePath();
            //保存文件名称
            mCurrentPhotoName = tempFile.getName();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //显示记录的图片在imageView中
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // 从文件中加载拍摄的图片
            Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
            // 将图片设置到ImageView中显示
            ImageView imgVw = (ImageView) findViewById(R.id.imageView);
            imgVw.setImageBitmap(bitmap);
            TextView picNameTv = (TextView) findViewById(R.id.pic_name);
            picNameTv.setText(mCurrentPhotoName);
        }
        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK && data != null) {
            // 在此处对所选图像进行相应的处理
            Uri selectedImageUri = data.getData();
            String imagePath = getImagePath(selectedImageUri);
            File imageFile = new File(imagePath);
            saveImageToFile(imageFile);
            // 从文件中加载拍摄的图片
            Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
            // 将图片设置到ImageView中显示
            ImageView imgVw = (ImageView) findViewById(R.id.imageView);
            imgVw.setImageBitmap(bitmap);
            TextView picNameTv = (TextView) findViewById(R.id.pic_name);
            picNameTv.setText(mCurrentPhotoName);
        }
    }
    //解决屏幕旋转导致动态组件和数据消失
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //获取布局
        LinearLayout textLayout = (LinearLayout)findViewById(R.id.imageLayout);
        //遍历linearLayout
        for (int i = 10; i < textLayout.getChildCount(); i+=3) {
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
        LinearLayout textLayout = (LinearLayout)findViewById(R.id.imageLayout);
        int index = savedInstanceState.getInt("index");
        // 恢复组件状态
        for (int i = 10; i < index; i += 3) {
            String text = savedInstanceState.getString("text"+i);
            String edit = savedInstanceState.getString("edit"+i);
            TextView tv = new EditText(imageData.this);
            tv.setText(text);
            textLayout.addView(tv);
            tv.setEnabled(false);
            TextView dt = new EditText(imageData.this);
            dt.setText(edit);
            textLayout.addView(dt);
            Button bt = new Button(imageData.this);
            bt.setText("删除");
            textLayout.addView(bt);
        }
    }

    //计算图像文件数量
    public static int countJpegFiles(File dir) {
        int count = 0;
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && isJpegFile(file)) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    private static boolean isJpegFile(File file) {
        String extension = getExtension(file);
        return extension != null && extension.equalsIgnoreCase("jpg");
    }

    private static String getExtension(File file) {
        String name = file.getName();
        int lastDot = name.lastIndexOf(".");
        if (lastDot >= 0) {
            return name.substring(lastDot + 1);
        } else {
            return null;
        }
    }



}