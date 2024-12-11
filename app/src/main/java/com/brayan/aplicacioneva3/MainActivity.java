package com.brayan.aplicacioneva3;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.Button;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText txtCodigo, txtNombre, txtAdmin, txtDireccion;
    private ListView lista;
    private Spinner spUsuario;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        CargarListaFirestore();

        db = FirebaseFirestore.getInstance();

        txtCodigo = findViewById(R.id.txtCodigo);
        txtNombre = findViewById(R.id.txtNombre);
        txtAdmin = findViewById(R.id.txtAdmin);
        txtDireccion = findViewById(R.id.txtDireccion);
        spUsuario = findViewById(R.id.spUsuario);
        lista = findViewById(R.id.Lista);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, TiposUsuario);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spUsuario.setAdapter(adapter);

        // Configurar el botón para ir a la actividad MQTT
        Button botonMqtt = findViewById(R.id.botonMqtt);
        botonMqtt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                irAMqtt();
            }
        });
    }

    String[] TiposUsuario = {"Joven", "Adulto", "Viejo"};

    public void enviarDatosFirestore(View view) {
        String codigo = txtCodigo.getText().toString();
        String nombre = txtNombre.getText().toString();
        String admin = txtAdmin.getText().toString();
        String direccion = txtDireccion.getText().toString();
        String TipoUsuario = spUsuario.getSelectedItem().toString();

        Map<String, Object> usuario = new HashMap<>();
        usuario.put("codigo", codigo);
        usuario.put("nombre", nombre);
        usuario.put("admin", admin);
        usuario.put("direccion", direccion);
        usuario.put("TipoUsuario", TipoUsuario);

        db.collection("usuarios")
                .document(codigo)
                .set(usuario)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(MainActivity.this, "Datos enviados a Firestore correctamente", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(MainActivity.this, "Error al enviar datos a Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    // Botón Cargar Lista
    public void CargarLista(View view) {
        CargarListaFirestore();
    }

    // Método Cargar Lista
    public void CargarListaFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("usuarios")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<String> listaUsuarios = new ArrayList<>();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String linea = "||" + document.getString("codigo") + "||" +
                                        document.getString("nombre") + "||" +
                                        document.getString("usuario") + "||" +
                                        document.getString("direccion");
                                listaUsuarios.add(linea);
                            }

                            ArrayAdapter<String> adaptador = new ArrayAdapter<>(MainActivity.this,
                                    android.R.layout.simple_list_item_1, listaUsuarios);
                            lista.setAdapter(adaptador);
                        } else {
                            Log.e("TAG", "Error al obtener datos de Firestore", task.getException());
                        }
                    }
                });
    }

    // Método para ir a la actividad MQTT
    private void irAMqtt() {
        Intent intent = new Intent(this, MainMqtt.class);
        startActivity(intent);

    }

}