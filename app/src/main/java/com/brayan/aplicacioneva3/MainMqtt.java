package com.brayan.aplicacioneva3;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainMqtt extends AppCompatActivity {

    private static String mqttHost = "tcp://evaluacion3.cloud.shiftr.io:1883";
    private static String IdUsuario = "appAndroid";
    private static String Topico = "Mensaje";
    private static String User = "evaluacion3";
    private static String Pass = "DyRSpexZJwUdzDBS";

    private TextView textView;
    private EditText editTextMesagge;
    private Button botonEnvio;
    private Button botonRegresar; // Botón para regresar

    private MqttClient mqttClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mqtt_layout);

        textView = findViewById(R.id.textView);
        editTextMesagge = findViewById(R.id.txtMensaje);
        botonEnvio = findViewById(R.id.botonEnvioMensaje);
        botonRegresar = findViewById(R.id.botonRegresar); // Inicializar el botón

        try {
            mqttClient = new MqttClient(mqttHost, IdUsuario, null);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(User); // Establecer el nombre de usuario correctamente
            options.setPassword(Pass.toCharArray());
            mqttClient.connect(options);
            Toast.makeText(this, "Aplicación conectada al servidor MQTT", Toast.LENGTH_SHORT).show();

            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    Log.d("MQTT", "Conexión perdida: " + cause.getMessage());
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    String msg = new String(message.getPayload());
                    textView.setText("Mensaje recibido: " + msg);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    // No se necesita implementar para este caso
                }
            });

            mqttClient.subscribe(Topico);

        } catch (MqttException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al conectar al servidor MQTT: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        // Configurar el botón de enviar mensaje
        botonEnvio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarMensaje();
            }
        });

        // Configurar el botón para regresar a MainActivity
        botonRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regresarAMainActivity();
            }
        });
    }

    private void enviarMensaje() {
        String mensaje = editTextMesagge.getText().toString();
        if (!mensaje.isEmpty()) {
            try {
                mqttClient.publish(Topico, new MqttMessage(mensaje.getBytes()));
                Toast.makeText(this, "Mensaje enviado: " + mensaje, Toast.LENGTH_SHORT).show();
                editTextMesagge.setText(""); // Limpiar el campo de texto
            } catch (MqttException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al enviar mensaje: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Por favor ingrese un mensaje", Toast.LENGTH_SHORT).show();
        }
    }

    private void regresarAMainActivity() {
        Intent intent = new Intent(MainMqtt.this, MainActivity.class);
        startActivity(intent);
        finish(); // Cerrar MqttActivity
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

}
