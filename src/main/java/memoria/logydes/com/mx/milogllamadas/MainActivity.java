package memoria.logydes.com.mx.milogllamadas;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URI;
import java.text.DateFormat;

public class MainActivity extends AppCompatActivity {

    private Activity activity;
    private static final int CODIGO_SOLICITUD_PERMISO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;
    }

    public void mostrarLlamadas(View v){

        if ( checarStatusPermiso()  ){
            consultarCPLlamadas();
        }else{
            solicitarPermiso();
        }

    }

    public void solicitarPermiso(){
        // Read Call Log
        boolean solicitarPermisoRCL = ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_CALL_LOG);
        // Write Call Log
        boolean solicitarPermisoWCL = ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_CALL_LOG);

        if (solicitarPermisoRCL && solicitarPermisoWCL){
            Toast.makeText(MainActivity.this, "Los Permisos fueron otorgado", Toast.LENGTH_SHORT).show();
        }else{
            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.READ_CALL_LOG,Manifest.permission.WRITE_CALL_LOG}, CODIGO_SOLICITUD_PERMISO);
        }

    }

    public boolean checarStatusPermiso(){

        boolean permisoRCL = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED;
        boolean permisoWCL = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALL_LOG) == PackageManager.PERMISSION_GRANTED;

        if ( permisoRCL && permisoWCL){
            return true;
        }else{
            return false;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case CODIGO_SOLICITUD_PERMISO:
                if ( checarStatusPermiso() ){
                    Toast.makeText(MainActivity.this, "Ya estan activos los permisos", Toast.LENGTH_SHORT).show();
                    consultarCPLlamadas();
                }else{
                    Toast.makeText(MainActivity.this, "No se activo el permiso", Toast.LENGTH_SHORT).show();
                }
            break;
        }
    }

    public void consultarCPLlamadas(){

        TextView tvLlamanas = (TextView) findViewById(R.id.tvLlamadas);
        tvLlamanas.setText("");

        Uri direccionUriLlamadas = CallLog.Calls.CONTENT_URI;

        // Numero, Fecha, Tipo, Duracion

        String[] campos = {
                CallLog.Calls.NUMBER,
                CallLog.Calls.DATE,
                CallLog.Calls.TYPE,
                CallLog.Calls.DURATION
        };

        ContentResolver cr = getContentResolver();
        Cursor reg = cr.query(direccionUriLlamadas, campos, null, null, CallLog.Calls.DATE + " DESC");

        while (reg.moveToNext()){

            // Obtenemos los datos apartir de los indices de la columna.
            String numero = reg.getString(reg.getColumnIndex(campos[0]));
            Long fecha = reg.getLong(reg.getColumnIndex(campos[1]));
            int Tipo = reg.getInt(reg.getColumnIndex(campos[2]));
            String Duracion = reg.getString(reg.getColumnIndex(campos[3]));

            // Validación Tipo LLamada
            String cTipo = "";
            switch (Tipo){
                case CallLog.Calls.INCOMING_TYPE:
                    cTipo = getResources().getString(R.string.CallEntrada);
                     break;
                case CallLog.Calls.MISSED_TYPE:
                    cTipo = getResources().getString(R.string.CallSalida);
                    break;
                case CallLog.Calls.OUTGOING_TYPE:
                    cTipo = getResources().getString(R.string.CallPerdida);
                    break;
                default:
                    cTipo = getResources().getString(R.string.CallDesconocida);
                    break;

            }

            String Detalle = getResources().getString(R.string.lblNumero) + numero +"\n"+
                            getResources().getString(R.string.lblFecha) + android.text.format.DateFormat.format("dd-mm.yyyy k:mm",fecha) +"\n"+
                            getResources().getString(R.string.lblTipo) + cTipo +"\n"+
                            getResources().getString(R.string.lblDuracion) + Duracion + "s."+"\n\n";

            tvLlamanas.append(Detalle);

        }


    }

}
