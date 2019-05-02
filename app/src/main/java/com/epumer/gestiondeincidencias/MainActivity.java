package com.epumer.gestiondeincidencias;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity
    implements ListaDeIncidencias.ListaDeIncidenciasListener, AddIncidencia.AddIncidenciaListener {

    MediaPlayer mediaPlayer;
    boolean isPlaying = false;
    ChildEventListener childEventListener;
    HashMap<String, String> cachedFiles;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar)findViewById(R.id.menu_bar));
        cachedFiles = new HashMap<>();
        mediaPlayer = new MediaPlayer();
        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Incidencia incidencia = dataSnapshot.getValue(Incidencia.class);
                incidencia.setKey(dataSnapshot.getKey());

                ListaDeIncidencias ldi = (ListaDeIncidencias) getSupportFragmentManager().findFragmentByTag("ListaDeIncidencias");

                if (ldi != null) {
                    ldi.addIncidencia(incidencia);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mostrarListaDeIncidencias();
        toggleCancion();
    }

    private void mostrarListaDeIncidencias() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, ListaDeIncidencias.newInstance(), "ListaDeIncidencias")
                .commit();

        FirebaseDatabase.getInstance().getReference().child("tareas").removeEventListener(childEventListener);
        FirebaseDatabase.getInstance().getReference().child("tareas").addChildEventListener(childEventListener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.config:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, AddIncidencia.newInstance(), "AddIncidencia")
                        .commit();
                return true;
            case R.id.musica:
                toggleCancion();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    public void toggleCancion() {
        if (isPlaying) {
            mediaPlayer.stop();
            isPlaying = false;
        } else {
            reproducirCancion("patata.mp3");
        }
    }

    public void reproducirCancion(String urlCancion) {
        FirebaseStorage.getInstance()
                .getReference()
                .child(urlCancion)
                .getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        mediaPlayer.reset();
                        try {
                            mediaPlayer.setDataSource(uri.toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                isPlaying = true;
                                mediaPlayer.start();
                            }
                        });
                        mediaPlayer.prepareAsync();
                    }
                });
    }

    @Override
    public void saveIncidencia(Incidencia incidencia) {
        FirebaseDatabase.getInstance().getReference().child("tareas").push().setValue(incidencia);
    }

    @Override
    public void putImage() {
        seleccionarImagen();
    }

    @Override
    public void onBackPressed() {
        mostrarListaDeIncidencias();
    }

    public void enviarImagen(Bitmap bitmap, String name) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageRef.child( name + ".png");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.i("EPUMERLOG", exception.getMessage());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
                Log.i("EPUMERLOG", "Imagen enviada");
            }
        });
    }

    public void seleccionarImagen() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(intent, 10);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bitmap bitmap = null;

        if(requestCode == 10 && resultCode == RESULT_OK){

            Uri uri;
            uri = data.getData();

            try {

                bitmap = MediaStore.Images.Media
                        .getBitmap(getContentResolver(), uri);


            }catch (Exception e){
                e.printStackTrace();
            }
        } else if (requestCode == 20 && resultCode == RESULT_OK){

            bitmap = (Bitmap) data.getExtras().get("data");

        }

        if(bitmap != null){
            String tag = "ENVIAR_IMAGEN";

            String name = UUID.randomUUID().toString();

            enviarImagen(bitmap, name);
            AddIncidencia addIncidencia = (AddIncidencia) getSupportFragmentManager().findFragmentByTag("AddIncidencia");
            addIncidencia.putImage(bitmap, name + ".png");
        }

    }

    @Override
    public void ponerImagen(ImageView imagen, String url) {
        if (imagen == null || url == null) {
            return;
        }
        HiloDescargaBitmap hdb = new HiloDescargaBitmap(imagen);
        hdb.execute(url);
    }

    @Override
    public void resolverIncidencia(Incidencia incidencia, boolean isChecked) {
        FirebaseDatabase
                .getInstance()
                .getReference()
                .child("tareas")
                .child(incidencia.getKey())
                .child("resuelta")
                .setValue(isChecked);
    }

    public class HiloDescargaBitmap extends AsyncTask<String, Void, Bitmap> {
        /* Este hilo se encarga de poner la imagen en el imageView */

        ImageView imagen;
        File localFile;
        boolean success, end;

        public HiloDescargaBitmap(ImageView imagen) {
            this.imagen = imagen;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            end = false;
            if (cachedFiles.containsKey(strings[0])) {
                return BitmapFactory.decodeFile(cachedFiles.get(strings[0]));
            }
            StorageReference ref = FirebaseStorage.getInstance().getReference().child(strings[0]);
            localFile = null;
            try {
                localFile = File.createTempFile(strings[0], "png");
            } catch (IOException e) {
                e.printStackTrace();
            }

            ref.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    success = true;
                    end = true;
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    success = false;
                    end = true;
                }
            });

            /* Espero a que se descargue la imagen para ponerla en el ImageView */
            while (true) {
                if (end) {
                    if (success) {
                        /* Guardo la dirección del archivo temporal para no descargarlo más de una
                        vez */
                        cachedFiles.put(strings[0], localFile.getPath());
                        return BitmapFactory.decodeFile(localFile.getPath());
                    } else {
                        return null;
                    }
                } else {
                    try {
                        //Por alguna razón si no paro el hilo un poco no se carga nunca la imagen
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                imagen.setImageBitmap(bitmap);
            }
        }
    }
}
