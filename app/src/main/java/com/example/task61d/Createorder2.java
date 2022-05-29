package com.example.task61d;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.task61d.Database.DatabaseHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class Createorder2 extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    Bitmap bitmap;
    byte[] photo;
    EditText Weight;
    EditText Longth;
    EditText Width;
    EditText Height;
    Button Create;
    ImageButton Upload;
    EditText goodstype;
    String name,location,itemtype,cartype;
    int year,month,day,hour,minute;
    Double Weight1,Longth1,Width1,Height1;
    Button Trucks,Van,Refrigeratedtruck,Minitruck,Other2;
    protected static final int CHOOSE_PICTURE = 0;
    protected static final int TAKE_PICTURE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createorder2);
        findId();
        getvalue();
        dbHelper = new DatabaseHelper(this, "database", null, 1);
        class ButtonListener implements View.OnClickListener {
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.Trucks:
                        cartype="Truck";
                        resetcarbackground();
                        Trucks.setSelected(true);
                        break;
                    case R.id.Refrigeratedtruck:
                        cartype="Refrigeratedtruck";
                        resetcarbackground();
                        Refrigeratedtruck.setSelected(true);
                        break;
                    case R.id.Van:
                        cartype="Van";
                        resetcarbackground();
                        Van.setSelected(true);
                        break;
                    case R.id.Minitruck:
                        cartype="Minitruck";
                        resetcarbackground();
                        Minitruck.setSelected(true);
                        break;
                    case R.id.Other2:
                        cartype="Other";
                        resetcarbackground();
                        Other2.setSelected(true);
                        break;
                    //When clicking on an image, a dialog box will pop up
                    // asking the user to take or upload an image
                    case R.id.goods:
                        showChoosePicDialog();
                    default:
                        break;
                }
            }
        }
        //Click on a picture of an item type to upload or take a picture
        Upload.setOnClickListener(new ButtonListener());
        Trucks.setOnClickListener(new ButtonListener());
        Van.setOnClickListener(new ButtonListener());
        Refrigeratedtruck.setOnClickListener(new ButtonListener());
        Minitruck.setOnClickListener(new ButtonListener());
        Other2.setOnClickListener(new ButtonListener());
        //After the user clicks the Create button, the data is checked for completeness
        // and then uploaded to the database
        Create.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                Weight1= Double.valueOf(Weight.getText().toString());
                Height1=Double.valueOf(Height.getText().toString());
                Longth1=Double.valueOf(Longth.getText().toString());
                Width1=Double.valueOf(Width.getText().toString());
                photo=null;
                if(Upload.getDrawable()!=null) {
                    photo=getBytes(Upload.getDrawable());
                   photo= imagemTratada(photo);
                }
                itemtype=goodstype.getText().toString();
                if(cartype!=null&&itemtype!=null&&Weight1!=null&&Height1!=null&&Longth1!=null&&Width1!=null){
                    db = dbHelper.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    putvalue(values);
                    long result = db.insert("order2", null, values);
                    db.close();
                    dbHelper.close();
                    if (result > 0) {
                        Toast.makeText(Createorder2.this, "Order created successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Createorder2.this, Home.class);
                        startActivity(intent);
                        finish(); }
                }
                else{
                    Toast.makeText(Createorder2.this, "Bad", Toast.LENGTH_SHORT).show();
                }
                 }
            });
    }
      public void findId(){
          Upload=findViewById(R.id.goods);
          goodstype=findViewById(R.id.goodstype);
          Trucks=findViewById(R.id.Trucks);
          Create=findViewById(R.id.Create);
          Van=findViewById(R.id.Van);
          Refrigeratedtruck=findViewById(R.id.Refrigeratedtruck);
          Minitruck=findViewById(R.id.Minitruck);
          Weight=findViewById(R.id.Weight);
          Height=findViewById(R.id.Height);
          Width=findViewById(R.id.Width);
          Longth=findViewById(R.id.Length);
          Other2=findViewById(R.id.Other2);
      }
      //Get the parameters filled in by the user from the previous order creation activity
      public void getvalue(){
          Intent getIntent = getIntent();
          name = getIntent.getStringExtra("name");
          location = getIntent.getStringExtra("location");
          year= getIntent.getIntExtra("year",0);
          month= getIntent.getIntExtra("month",0);
          day= getIntent.getIntExtra("day",0);
          hour= getIntent.getIntExtra("hour",0);
          minute= getIntent.getIntExtra("minute",0);
      }
      //After the user clicks on another button, the rest of
      // the buttons revert to an unclicked state
      public void resetcarbackground(){
        Trucks.setSelected(false);
        Refrigeratedtruck.setSelected(false);
        Minitruck.setSelected(false);
        Van.setSelected(false);
        Other2.setSelected(false);
    }
      public void putvalue(ContentValues values){
        values.put("cartype", cartype);
        values.put("itemtype", itemtype);
        values.put("photo", photo);
        values.put("weight", Weight1);
        values.put("receiver", name);
        values.put("height", Height1);
        values.put("length", Longth1);
        values.put("width", Width1);
        values.put("year", year);
        values.put("month", month);
        values.put("day", day);
        values.put("hour", hour);
        values.put("minute", minute);
        values.put("userid", Global.id);
    }
    //Classification of images using ML Kit
      private void labelImages(Bitmap bitmap) {
        //Set the parameters required for runtime classification
        ImageLabelerOptions options =
                new ImageLabelerOptions.Builder()
                        .setConfidenceThreshold(0.8f)
                        .build();
        InputImage image=InputImage.fromBitmap(bitmap,0);
        ImageLabeler labeler = ImageLabeling.getClient(options);
        //Processing of the input image
        Task<List<ImageLabel>> result =
                labeler.process(image).addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() {
                    @Override
                    //When the classification is successful and the results are returned,
                    // set the most likely result to the type of item
                    public void onSuccess(List<ImageLabel> imageLabels) {
                        if (imageLabels.size() > 0) {
                            for (ImageLabel label : imageLabels) {
                                goodstype.setText(label.getText());
                                break;
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });
    }
    //A dialog box pops up when the user clicks to upload an image of the item.
    protected void showChoosePicDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Set avatar");
        String[] items = { "Select a local photo", "Photo shoot" };
        builder.setNegativeButton("Cancellation", null);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    //The following statement is executed when the user selects the photo option
                    case CHOOSE_PICTURE:
                        //Check permissions, if you have them, execute the else statement, if not, request them
                        int readPermissionCheck = ContextCompat.checkSelfPermission(Createorder2.this, Manifest.permission.READ_EXTERNAL_STORAGE);
                        int writePermissionCheck = ContextCompat.checkSelfPermission(Createorder2.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        if (readPermissionCheck != PackageManager.PERMISSION_GRANTED && writePermissionCheck != PackageManager.PERMISSION_GRANTED) {
                            String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                            ActivityCompat.requestPermissions(
                                    Createorder2.this,
                                    permissions,
                                    0);
                        }
                        else {
                            Intent intent1 = new Intent(Intent.ACTION_PICK);
                            intent1.setType("image/*");
                            startActivityForResult(intent1, 0);
                        break;
                        }
                        //The following statement is executed when the user selects the option to take a photo
                    case TAKE_PICTURE:
                        int photoPermissionCheck=ContextCompat.checkSelfPermission(Createorder2.this, Manifest.permission.CAMERA);
                        if(photoPermissionCheck!= PackageManager.PERMISSION_GRANTED) {
                                String[] permissions = new String[]{Manifest.permission.CAMERA};
                                ActivityCompat.requestPermissions(
                                        Createorder2.this,
                                        permissions,
                                        0);
                        }
                        else {
                            Intent openCameraIntent = new Intent(
                                    MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(openCameraIntent, 1);
                            break;
                        }
                }
            }
        });
        builder.create().show();
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //If the user selects a photo successfully, set up the photo and sort the photo.
        if (resultCode == RESULT_OK&&requestCode==0) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                labelImages(bitmap);
                Upload.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
         }
        //If the user takes the photo successfully,the photo size is
        // processed to set the photo and the photo is sorted.
        if (resultCode == RESULT_OK&&requestCode==1) {
            bitmap = (Bitmap) data.getExtras().get("data");
            int dimension = Math.min( bitmap.getWidth(), bitmap.getHeight());
            bitmap = ThumbnailUtils.extractThumbnail( bitmap,dimension,dimension);
            Upload.setImageBitmap( bitmap);
            labelImages(bitmap);
         }
        }
    //Convert the set image to byte[] to upload to the database
    public static byte[] getBytes(Drawable drawable) {
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 1, bos);
        return bos.toByteArray();
    }
    //Compression of oversized images
    private byte[] imagemTratada(byte[] imagem_img){
        while (imagem_img.length > 500000){
            Bitmap bitmap = BitmapFactory.decodeByteArray(imagem_img, 0, imagem_img.length);
            Bitmap resized = Bitmap.createScaledBitmap(bitmap, (int)(bitmap.getWidth()*0.8), (int)(bitmap.getHeight()*0.8), true);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            resized.compress(Bitmap.CompressFormat.PNG, 100, stream);
            imagem_img = stream.toByteArray();
        }
        return imagem_img;
    }
}
