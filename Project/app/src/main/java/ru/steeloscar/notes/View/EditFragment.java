package ru.steeloscar.notes.View;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ru.steeloscar.notes.BuildConfig;
import ru.steeloscar.notes.Contract.MainContract;
import ru.steeloscar.notes.R;
import ru.steeloscar.notes.Repository.DB.Database.NotesDB;


public class EditFragment extends Fragment implements ActivityFragmentContract.ActivityEditInterface,MainContract.EditFragment {

    private TextInputEditText noteTitle;
    private TextInputEditText noteDetail;
    private CardView chosenColorCardView;
    private CardView palletContainer;
    private ImageView noteImage;
    private CardView imageCardView;
    private TextInputLayout til_title;
    private TextInputLayout til_detail;

    private ActivityFragmentContract.EditActivityInterface editActivityInterface;
    private MainContract.EditFragmentPresenter editFragmentPresenter;

    private static final String APP_PREFERENCES = "note_settings";
    private static final int REQUEST_PERMISSIONS_EXTERNAL_STORAGE = 1001;
    private static final int REQUEST_PERMISSIONS_CAMERA = 1002;
    private static final int REQUEST_IMAGE_GALLERY = 2001;
    private static final int REQUEST_IMAGE_CAMERA = 2002;
    private static final int DIALOG_IMAGE_GALLERY = 0;
    private static final int DIALOG_IMAGE_CAMERA = 1;
    private static final int DIALOG_IMAGE_DELETE = 2;

    private SharedPreferences settings;
    private String restoreTitle;
    private String restoreDetail;
    private Bitmap restoreBitmap;
    private int restoreColor;
    private String dateText;
    private int id;
    private int position;
    private int color;
    private Bitmap bitmap = null;
    private Boolean palletIsOpen = false;
    private Boolean isEdit;
    private Uri uri;

    EditFragment(Boolean isEdit) {
        this.isEdit = isEdit;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        editActivityInterface = (ActivityFragmentContract.EditActivityInterface) context;
        settings = context.getSharedPreferences(APP_PREFERENCES,Context.MODE_PRIVATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view =inflater.inflate(R.layout.fragment_edit, container,false);

        editFragmentPresenter = new ru.steeloscar.notes.Presenter.EditFragmentPresenter(this, settings);
        noteTitle = view.findViewById(R.id.note_title);
        noteDetail = view.findViewById(R.id.note_detail);
        chosenColorCardView = view.findViewById(R.id.chosenColor);
        palletContainer = view.findViewById(R.id.palletContainer);
        GridView gridView = view.findViewById(R.id.palletGridView);
        ImageButton attachImageButton = view.findViewById(R.id.attachImageButton);
        noteImage = view.findViewById(R.id.edit_image);
        imageCardView = view.findViewById(R.id.imageCardView);
        til_title = view.findViewById(R.id.til_title);
        til_detail = view.findViewById(R.id.til_detail);

        color = getResources().getColor(R.color.palletColor1);

        if (!isEdit) editFragmentPresenter.getLastNoteIdSharedPreferences();

        final PalletsArrayAdapter palletsArrayAdapter = new PalletsArrayAdapter(getContext(), Pallet.getPallet());
        gridView.setAdapter(palletsArrayAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                color = getResources().getColor(palletsArrayAdapter.getColorViewHolder(position));
                chosenColorCardView.setCardBackgroundColor(color);
            }
        });

        chosenColorCardView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if (!palletIsOpen) {
                    palletContainer.setVisibility(View.VISIBLE);
                    animationPallet(palletContainer, 1F);
                    palletIsOpen = true;
                } else {
                    animationPallet(palletContainer, 0F);
                    palletIsOpen = false;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            palletContainer.setVisibility(View.GONE);
                        }
                    },150);
                }

            }
        });

        attachImageButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                builder.setTitle(R.string.attach_image)
                        .setItems(R.array.attach_array, new DialogInterface.OnClickListener(){

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                clickItemDialog(which);
                            }
                        })
                        .create()
                        .show();

            }
        });

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        editFragmentPresenter.destroyDisposables();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        inflater.inflate(R.menu.create_note, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

         if (item.getItemId() == R.id.action_save){

             Boolean isCorrect = checkTextData();

             if (isCorrect) {
                 if (isEdit) {
                     editActivityInterface.onViewFragmentUpdateNote(noteTitle.getText().toString(), noteDetail.getText().toString(), position, color, bitmap);
                     if (!restoreTitle.equals(noteTitle.getText().toString()) || !restoreDetail.equals(noteDetail.getText().toString()) || restoreColor != color || restoreBitmap != bitmap) {
                         editFragmentPresenter.onUpdateDBNote();
                     }
                 } else {
                     Date currentDate = new Date();
                     DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault());
                     dateText = dateFormat.format(currentDate);

                     editActivityInterface.onEditFragmentSaveClicked(noteTitle.getText().toString(), noteDetail.getText().toString(), dateText, id + 1, color, bitmap);
                     editFragmentPresenter.onSaveDBNote();
                 }
             }
         }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public NotesModel saveNotesDB() {
        return new NotesModel(noteTitle.getText().toString(), noteDetail.getText().toString(), dateText, id+1, color, bitmap);
    }

    @Override
    public NotesModel updateNoteDB() {
        return new NotesModel(noteTitle.getText().toString(), noteDetail.getText().toString(), dateText, id, color, bitmap) ;
    }

    @Override
    public void setID(int id) {
        this.id = id;
    }

    @Override
    public void setNoteEditFragment(String title, String detail, String dateText, int position, int id, int color, Bitmap bitmap) {
        noteTitle.setText(title);
        noteDetail.setText(detail);
        noteDetail.requestFocus();
        restoreTitle = title;
        restoreDetail = detail;
        this.dateText = dateText;
        this.id = id;
        this.position = position;
        this.color = color;
        restoreColor = color;
        chosenColorCardView.setCardBackgroundColor(color);
        this.bitmap = bitmap;
        restoreBitmap = bitmap;
        if (bitmap != null) {
            noteImage.setImageBitmap(bitmap);
            imageCardView.setVisibility(View.VISIBLE);
        }
    }

    private void animationPallet(CardView palletCard, Float value) {
        final ObjectAnimator animationX = ObjectAnimator.ofFloat(palletCard, "scaleX", value);
        final ObjectAnimator animationY = ObjectAnimator.ofFloat(palletCard, "scaleY", value);
        final AnimatorSet set = new AnimatorSet();

        set.play(animationX)
                .with(animationY);

        set.setDuration(250L);
        set.setInterpolator(new DecelerateInterpolator());
        set.start();
    }

    private void clickItemDialog(int which){
        switch (which) {
            case DIALOG_IMAGE_GALLERY: {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && (ContextCompat.checkSelfPermission(getContext(),Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSIONS_EXTERNAL_STORAGE);
                } else {
                    loadImageFromGallery();
                }
                break;
            }
            case DIALOG_IMAGE_CAMERA: {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ((ContextCompat.checkSelfPermission(getContext(),Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED)
                        || (ContextCompat.checkSelfPermission(getContext(),Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED))) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_PERMISSIONS_CAMERA);
                } else {
                    loadImageFromCamera();
                }
                break;
            }
            case DIALOG_IMAGE_DELETE: {
                bitmap = null;
                imageCardView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_PERMISSIONS_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadImageFromGallery();}
                else Toast.makeText(getActivity(), getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();
                break;
            }
            case REQUEST_PERMISSIONS_CAMERA: {
                if (grantResults.length > 0 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {loadImageFromCamera();}
                else Toast.makeText(getActivity(), getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();
                break;
            }
        }
    }

    private void loadImageFromGallery(){
        final Intent galleryIntent = new Intent(Intent.ACTION_PICK);

        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,REQUEST_IMAGE_GALLERY);
    }

    private void loadImageFromCamera() {

        final Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File imageFile = editFragmentPresenter.createTempFile();

            if (imageFile != null) {
                uri = FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID + ".provider",imageFile);

                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,uri);
                cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivityForResult(cameraIntent,REQUEST_IMAGE_CAMERA);
            }
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode){
                case REQUEST_IMAGE_GALLERY:{
                    assert data != null;
                    noteImage.setImageURI(data.getData());
                    imageCardView.setVisibility(View.VISIBLE);
                    break;
                }
                case REQUEST_IMAGE_CAMERA: {
                    noteImage.setImageURI(uri);
                    imageCardView.setVisibility(View.VISIBLE);
                    break;
                }
            }

            bitmap = ((BitmapDrawable)noteImage.getDrawable()).getBitmap();
        }
    }

    @Override
    public void failedCreateTempFile(){
            Toast.makeText(getContext(), getString(R.string.error_create_file), Toast.LENGTH_SHORT).show();
    }

    @Override
    public NotesDB createDB() {
        return Room.databaseBuilder(getContext(), NotesDB.class,"Notes-database").build();
    }

    private Boolean checkTextData(){
        String title = noteTitle.getText().toString().trim();
        String detail = noteDetail.getText().toString().trim();

        if (title.isEmpty()) {
            til_title.setError(getString(R.string.error_til));
            til_title.setErrorEnabled(true);

            return false;
        } else {
            til_title.setErrorEnabled(false);
        }

        if (detail.isEmpty()) {
            til_detail.setError(getString(R.string.error_til));
            til_detail.setErrorEnabled(true);

            return false;
        } else {
            til_detail.setErrorEnabled(false);
        }

        return true;
    }
}