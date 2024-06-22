package com.example.pm1_e1_0031_0101;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.pm1_e1_0031_0101.Configuracion.SQLiteConexion;
import com.example.pm1_e1_0031_0101.Configuracion.Usuarios;
import com.example.pm1_e1_0031_0101.Models.Contactos;

import java.util.ArrayList;

public class ActivityContactos extends AppCompatActivity {
    SQLiteConexion conexion;
    ListView listView;
    ArrayList<Contactos> listUser;
    EditText id;
    ArrayList<String> ArregloUser;

    Button btnAtras, btnimg, btneliminar, btnactualizar, btncompartir;
    private String telefono;
    private static final int REQUEST_CALL = 1;
    private boolean Selected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactos);

        // Establecemos una conexión a la base de datos
        conexion = new SQLiteConexion(this, Usuarios.namedb, null, 1);
        listView = findViewById(R.id.listUsuario);
        id = (EditText) findViewById(R.id.txtcid);


        Button btnAtras = (Button) findViewById(R.id.btnAtras);
        Button btneliminar = (Button) findViewById(R.id.btneliminar);
        Button btnimg = (Button) findViewById(R.id.btnVerimg);
        Button btnactualizar = (Button) findViewById(R.id.btnActualizar);
        Button btncompartir = (Button) findViewById(R.id.btnCompartir);

        btnAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        btnactualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //actualizar();
            }
        });

        GetContactos();

        ArrayAdapter<String> adp = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, ArregloUser);
        listView.setAdapter(adp);


        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> sele1, View selec2, int position, long select3) {

                    telefono =""+listUser.get(position).getTelefono();
                    Selected = true;

                    btneliminar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SQLiteDatabase db = conexion.getWritableDatabase();
                        String sql = "DELETE FROM usuarios WHERE id=" + listUser.get(position).getId();
                        db.execSQL(sql);
                        Intent i = new Intent(ActivityContactos.this, ActivityContactos.class);
                        startActivity(i);
                        finish();
                    }

                });
                    btnimg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.putExtra("id", listUser.get(position).getId());
                            startActivity(intent);
                        }
                    });

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        AlertDialog.Builder builder= new AlertDialog.Builder(ActivityContactos.this);
                        builder.setMessage("¿Quiere realizar una llamada?");
                        builder.setTitle("LLAMADA");

                        builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                mostrarnumero();

                            }
                        });

                        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(ActivityContactos.this,"LLamada no realizada", Toast.LENGTH_LONG).show();

                            }
                        });

                        AlertDialog dialog= builder.create();
                        dialog.show();
                    }
                });



                    btncompartir.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent share = new Intent(Intent.ACTION_SEND);
                            share.setType("text/plain");
                            share.putExtra(Intent.EXTRA_SUBJECT, listUser.get(position).getId() + ": " + listUser.get(position).getTelefono());
                            share.putExtra(Intent.EXTRA_TEXT, listUser.get(position).getTelefono());
                            startActivity(Intent.createChooser(share, "COMPARTIR"));
                        }
                    });
                }
            });

        id.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adp.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }

    private void GetContactos() {
        SQLiteDatabase db = conexion.getReadableDatabase();
        Contactos contact;
        listUser = new ArrayList<Contactos>();

        Cursor cursor = db.rawQuery(Usuarios.SelectTableUsuarios, null);
        while (cursor.moveToNext()) {
            contact = new Contactos();
            contact.setId(cursor.getInt(0));
            contact.setPais(cursor.getString(1));
            contact.setNombre(cursor.getString(2));
            contact.setTelefono(cursor.getInt(3));
            contact.setNota(cursor.getString(4));
            listUser.add(contact);
        }

        cursor.close();
        FillList();
    }

    private void FillList() {

        ArregloUser = new ArrayList<String>();

        for (int i = 0;  i < listUser.size(); i++){

            ArregloUser.add(listUser.get(i).getId() + " | "
                    +listUser.get(i).getNombre() + " | "
                    +listUser.get(i).getTelefono());

        }
    }

    private void mostrarnumero() {
        String numero = telefono;
        if (Selected) {
            if (ContextCompat.checkSelfPermission(ActivityContactos.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ActivityContactos.this,
                        new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
            } else {
                String n = "tel:" + numero;
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(n)));
            }
        } else {
            Toast.makeText(ActivityContactos.this, "Seleccione Un Contacto", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mostrarnumero();
            } else {
                Toast.makeText(this, "NO TIENE ACCESO", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
