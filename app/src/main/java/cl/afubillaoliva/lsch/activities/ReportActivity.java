package cl.afubillaoliva.lsch.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import cl.afubillaoliva.lsch.R;
import cl.afubillaoliva.lsch.api.ApiClient;
import cl.afubillaoliva.lsch.tools.MultipartRequest;
import cl.afubillaoliva.lsch.utils.SharedPreference;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import static cl.afubillaoliva.lsch.MainActivity.REQUEST_CODE;

public class ReportActivity extends AppCompatActivity {

    private final Context context = this;
    public static final int PICK_IMAGE = 0;

    private static final String USER_KEY = "user";
    private static final String USER_VAL = "ecdd7446cd389d";
    private static final String PASS_KEY = "pass";
    private static final String PASS_VAL = "fce0b8515205f4";

    private ImageView imageView, delete;
    private TextInputEditText editText;

    private Intent pickIntent, getIntent, chooserIntent;
    private File file;

    private final MediaType MEDIA_TYPE = MediaType.parse("image/*");

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        if(ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CODE);

        final SharedPreference mSharedPreferences = new SharedPreference(context);
        if(mSharedPreferences.loadNightModeState())
            setTheme(R.style.AppThemeDark);
        else
            setTheme(R.style.AppTheme);
        setContentView(R.layout.report_activity_layout);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        final Toolbar mToolbar = findViewById(R.id.toolbar);
        editText = findViewById(R.id.edit_text);
        imageView = findViewById(R.id.report_image);
        delete = findViewById(R.id.delete);

        if(mSharedPreferences.loadNightModeState())
            mToolbar.setTitleTextAppearance(context, R.style.ToolbarTypefaceDark);
        else
            mToolbar.setTitleTextAppearance(context, R.style.ToolbarTypefaceLight);
        mToolbar.setTitle(R.string.report_error);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        imageView.setOnClickListener(v -> {
            if(ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CODE);
            else
                if(hasNullOrEmptyDrawable(imageView)){

                    getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    getIntent.setType("image/*");

                    pickIntent = new Intent(Intent.ACTION_PICK);
                    pickIntent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");

                    chooserIntent = Intent.createChooser(getIntent, context.getResources().getString(R.string.image_select));
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

                    startActivityForResult(pickIntent, PICK_IMAGE);

                } else {
                    imageView.setImageBitmap(null);
                    imageView.setPadding(90,90,90,90);
                    imageView.setImageDrawable(getDrawable(R.drawable.ic_add_black_24dp));
                    delete.setVisibility(View.GONE);
                }
        });
    }

    public static boolean hasNullOrEmptyDrawable(ImageView iv){
        final BitmapDrawable bitmapDrawable = iv.getDrawable() instanceof BitmapDrawable ? (BitmapDrawable) iv.getDrawable() : null;
        return bitmapDrawable == null || bitmapDrawable.getBitmap() == null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK){
            InputStream inputStream = null;

            try {
                inputStream = context.getContentResolver().openInputStream(Objects.requireNonNull(data.getData()));
            } catch (FileNotFoundException e){
                e.printStackTrace();
            }

            final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            final Uri uri = data.getData();
            final String url = data.getData().toString();

            if (url.startsWith("content://com.google.android.apps.photos.content")){
                try {
                    final InputStream is = this.getContentResolver().openInputStream(uri);
                    if (is != null){
                        final Bitmap pictureBitmap = BitmapFactory.decodeStream(is);
                        file = new File(context.getCacheDir(), "image.png");
                        file.createNewFile();

                        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        pictureBitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
                        final byte[] bitmapdata = bos.toByteArray();

                        final FileOutputStream fos = new FileOutputStream(file);
                        fos.write(bitmapdata);
                        fos.flush();
                        fos.close();
                    }
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
            imageView.setPadding(0,0,0,0);
            imageView.setImageBitmap(bitmap);
            delete.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_report, menu);

        final MenuItem send = menu.findItem(R.id.send);
        final SpannableString s = new SpannableString(getResources().getString(R.string.send).toUpperCase());
        s.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorAccent)), 0, s.length(), 0);
        send.setTitle(s);

        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        final ProgressDialog progressDialog;
        switch (item.getItemId()){
            case android.R.id.home:
                if(Objects.requireNonNull(editText.getText()).length() != 0 || !hasNullOrEmptyDrawable(imageView)){
                    final AlertDialog dialog = new AlertDialog.Builder(context)
                            .setView(R.layout.confirmation_dialog_layout)
                            .show();
                    Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    final ExtendedFloatingActionButton positiveButton = dialog.findViewById(R.id.confirm_button);
                    assert positiveButton != null;
                    positiveButton.setText(R.string.positive_discard_dialog_button);
                    positiveButton.setOnClickListener(v -> {
                        dialog.dismiss();
                        finish();
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    });
                    final TextView dialogTitle = dialog.findViewById(R.id.dialog_title);
                    assert dialogTitle != null;
                    dialogTitle.setText(R.string.discard_dialog_report_title);
                    final TextView dialogSubtitle = dialog.findViewById(R.id.dialog_subtitle);
                    assert dialogSubtitle != null;
                    dialogSubtitle.setText(R.string.discard_dialog_report_subtitle);
                    final TextView negative = dialog.findViewById(R.id.negative_button);
                    assert negative != null;
                    negative.setText(R.string.negative_dialog_button);
                    negative.setOnClickListener(v -> dialog.dismiss());
                } else {
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
                break;
            case R.id.send:
                if(!hasNullOrEmptyDrawable(imageView) && Objects.requireNonNull(editText.getText()).length() != 0){
                    progressDialog = ProgressDialog.show(context, context.getResources().getString(R.string.sending), context.getResources().getString(R.string.sending_report), true);
                    final MultipartRequest request = new MultipartRequest(context, progressDialog);
                    request.addString(USER_KEY, USER_VAL);
                    request.addString(PASS_KEY, PASS_VAL);
                    request.addFile("file", RequestBody.create(MEDIA_TYPE, file), file.getName());
                    request.addString("message", Objects.requireNonNull(editText.getText()).toString());
                    request.execute(ApiClient.REPORT_URL);
                    request.onCode(getResources().getString(R.string.report_max_size), 504);
                } else if(!hasNullOrEmptyDrawable(imageView) && Objects.requireNonNull(editText.getText()).length() == 0){
                    progressDialog = ProgressDialog.show(context, context.getResources().getString(R.string.sending), context.getResources().getString(R.string.sending_report), true);
                    final MultipartRequest request = new MultipartRequest(context, progressDialog);
                    request.addString(USER_KEY, USER_VAL);
                    request.addString(PASS_KEY, PASS_VAL);
                    request.addFile("file", RequestBody.create(MEDIA_TYPE, file), file.getName());
                    request.execute(ApiClient.REPORT_URL);
                    request.onCode(getResources().getString(R.string.report_max_size), 504);
                } else if(hasNullOrEmptyDrawable(imageView) && Objects.requireNonNull(editText.getText()).length() != 0){
                    progressDialog = ProgressDialog.show(context, context.getResources().getString(R.string.sending), context.getResources().getString(R.string.sending_report), true);
                    final MultipartRequest request = new MultipartRequest(context, progressDialog);
                    request.addString(USER_KEY, USER_VAL);
                    request.addString(PASS_KEY, PASS_VAL);
                    request.addString("message", Objects.requireNonNull(editText.getText()).toString());
                    request.execute(ApiClient.REPORT_URL);
                    request.onCode(getResources().getString(R.string.report_max_size), 504);
                } else
                    Toast.makeText(context, context.getResources().getString(R.string.report_input_error), Toast.LENGTH_SHORT).show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        if(editText.isFocused())
            editText.clearFocus();
        else if(Objects.requireNonNull(editText.getText()).length() != 0 || !hasNullOrEmptyDrawable(imageView)){
            final AlertDialog dialog = new AlertDialog.Builder(context)
                    .setView(R.layout.confirmation_dialog_layout)
                    .show();
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            final ExtendedFloatingActionButton positiveButton = dialog.findViewById(R.id.confirm_button);
            assert positiveButton != null;
            positiveButton.setText(R.string.positive_discard_dialog_button);
            positiveButton.setOnClickListener(v -> {
                dialog.dismiss();
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            });
            final TextView dialogTitle = dialog.findViewById(R.id.dialog_title);
            assert dialogTitle != null;
            dialogTitle.setText(R.string.discard_dialog_report_title);
            final TextView dialogSubtitle = dialog.findViewById(R.id.dialog_subtitle);
            assert dialogSubtitle != null;
            dialogSubtitle.setText(R.string.discard_dialog_report_subtitle);
            final TextView negative = dialog.findViewById(R.id.negative_button);
            assert negative != null;
            negative.setText(R.string.negative_dialog_button);
            negative.setOnClickListener(v -> dialog.dismiss());
        }
        else {
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase){
        super.attachBaseContext(newBase);
        final Configuration override = new Configuration(newBase.getResources().getConfiguration());
        override.fontScale = 1.0f;
        applyOverrideConfiguration(override);
    }
}
