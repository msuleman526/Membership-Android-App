package com.appsnipp.e4solutions;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.pdf.PdfDocument;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.os.StrictMode;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintJob;
import android.print.PrintManager;
import android.print.pdf.PrintedPdfDocument;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.appsnipp.e4solutions.Steps.MainActivity;
import com.appsnipp.e4solutions.Visitors.VisitorListActivity;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.printer.discovery.DiscoveredPrinter;
import com.zebra.sdk.printer.discovery.DiscoveredPrinterNetwork;
import com.zebra.sdk.printer.discovery.DiscoveredPrinterUsb;
import com.zebra.sdk.printer.discovery.DiscoveryException;
import com.zebra.sdk.printer.discovery.DiscoveryHandler;
import com.zebra.sdk.printer.discovery.NetworkDiscoverer;
import com.zebra.sdk.printer.discovery.UsbDiscoverer;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TestPrinterActivity extends AppCompatActivity {

    RelativeLayout printerTest;
    Button printBtn, connectBtn;

    private static final String PRINTER_IP = "10.202.3.236"; // replace with the IP address of your printer
    private static final int PRINTER_PORT = 9100; // r
    private String printURL = "http://"+PRINTER_IP+":"+PRINTER_PORT+"/api/v1/cmd/print";


    Socket printerSocket = null;
    PrintManager printManager = null;
    PrintAttributes attributes = null;
    PrintDocumentAdapter printAdapter = null;

    EditText ipEdt, portEdt;

    TextView nameTxt;
    ImageView profileImg1;
    Button zebraUsbPrint;

    UsbManager mUsbManager;
    Button buttonRequestPermission;
    DiscoveredPrinterUsb discoveredPrinterUsb;
    DiscoveredPrinter discoveredPrinter;


    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    PendingIntent mPermissionIntent;
    boolean hasPermissionToCommunicate = false;

    // Catches intent indicating if the user grants permission to use the USB device
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d("PRINT Action", action);
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            hasPermissionToCommunicate = true;
                        }
                    }
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.working);
        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(mUsbReceiver, filter);

        findViewById(R.id.zebraUsbFind).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {

                    public void run() {
                        // Find connected printers
                        UsbDiscoveryHandler handler = new UsbDiscoveryHandler();
                        UsbDiscoverer.findPrinters(getApplicationContext(), handler);
                        Log.d("PRINT Find", "Finding Printers");
                        Log.d("PRINT Find", String.valueOf(handler.printers.size()));
                        try {
                            if (handler.printers != null && handler.printers.size() > 0)
                            {
                                discoveredPrinterUsb = handler.printers.get(0);
                                mUsbManager.requestPermission(discoveredPrinterUsb.device, mPermissionIntent);
                            }
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), e.getMessage() + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }).start();
            }
        });

        findViewById(R.id.zebraNetworkFind).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {

                    public void run() {
                        // Find connected printers
                        NetworkDiscoveryHandler handler = new NetworkDiscoveryHandler();
                        try {
                            NetworkDiscoverer.findPrinters(handler);
                            Log.d("PRINT SIZe", String.valueOf(handler.printers.size()));
                            try {
                                if (handler.printers != null && handler.printers.size() > 0)
                                {
                                    discoveredPrinter = handler.printers.get(0);
                                }
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), e.getMessage() + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                            }
                        } catch (DiscoveryException e) {
                            e.printStackTrace();
                            Toast.makeText(TestPrinterActivity.this, "No Network Printer Found", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).start();
            }
        });


        printerTest = findViewById(R.id.idCardView);
        printBtn = findViewById(R.id.printBtn);

       // printBtn.setVisibility(View.GONE);
        connectBtn = findViewById(R.id.connectBtn);
        nameTxt = findViewById(R.id.nameTxt);
        profileImg1 = findViewById(R.id.profileImage1);

        ipEdt = findViewById(R.id.ipEdt);
        portEdt = findViewById(R.id.portEdt);

        ipEdt.setText(PRINTER_IP);
        portEdt.setText(""+PRINTER_PORT);

        nameTxt.setText(getIntent().getStringExtra("name"));

        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ip = ipEdt.getText().toString();
                String port = portEdt.getText().toString();
                if(!ip.equals("") && !port.equals("")) {
                    try {
                        try {
                            InetAddress printerAddress = InetAddress.getByName(ip);
                            printerSocket = new Socket(printerAddress, Integer.parseInt(port));
                            printBtn.setVisibility(View.VISIBLE);
                            connectBtn.setVisibility(View.GONE);
                        } catch (UnknownHostException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } catch (Exception e) {
                        Toast.makeText(TestPrinterActivity.this, "Invalid IP / Port", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(TestPrinterActivity.this, "Invalid IP / Port", Toast.LENGTH_SHORT).show();
                }

            }
        });

        printBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                print();
            }
        });

        findViewById(R.id.print2Btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                printTest2();
            }
        });

        findViewById(R.id.print3Btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                printTest3();
            }
        });

        printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);

        zebraUsbPrint = findViewById(R.id.zebraUsbPrint);
        zebraUsbPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zebraUsbPrintClick();
            }
        });

        findViewById(R.id.zebraNetworkPrint).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zebraNetworkPrintClick();
            }
        });


        findViewById(R.id.restApiPrint).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bitmap bitmap = Bitmap.createBitmap(800, 1000, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                printerTest.draw(canvas);

                printURL = "http://"+ipEdt.getText().toString()+":"+portEdt.getText().toString()+"/api/v1/cmd/print";
                printViaApi(bitmap, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle success response
                        Toast.makeText(TestPrinterActivity.this, "Print Successfully.", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error response
                        Toast.makeText(TestPrinterActivity.this, ""+error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });
    }

    private void zebraUsbPrintClick() {

        if (hasPermissionToCommunicate)
        {
            try {
                Connection conn = discoveredPrinterUsb.getConnection();
                conn.open();

                Bitmap bitmap = Bitmap.createBitmap(800, 1000, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                printerTest.draw(canvas);
                // Create a new PrintAttributes object with the desired settings

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                conn.write(byteArray);
                conn.close();
            } catch (ConnectionException e) {
                Toast.makeText(getApplicationContext(), e.getMessage() + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(), "No permission to communicate / No Device connected", Toast.LENGTH_LONG).show();
        }
    }

    private void zebraNetworkPrintClick() {
        if(discoveredPrinter != null) {
            try {
                Connection conn = discoveredPrinter.getConnection();
                conn.open();

                Bitmap bitmap = Bitmap.createBitmap(800, 1000, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                printerTest.draw(canvas);
                // Create a new PrintAttributes object with the desired settings

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                conn.write(byteArray);
                conn.close();
            } catch (ConnectionException e) {
                Toast.makeText(getApplicationContext(), "No permission to communicate / No Device connected", Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(getApplicationContext(), "No permission to communicate / No Device connected", Toast.LENGTH_LONG).show();
        }
    }

    void print(){
        PrintAttributes.Builder builder = new PrintAttributes.Builder();
        builder.setColorMode(PrintAttributes.COLOR_MODE_COLOR);
        builder.setMediaSize(PrintAttributes.MediaSize.ISO_A4);
        builder.setMinMargins(new PrintAttributes.Margins(0, 0, 0, 0));
        builder.setResolution(new PrintAttributes.Resolution("1", "label", 300, 300));
        attributes = builder.build();

        printAdapter = new PrintDocumentAdapter() {
            @Override
            public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {
                // Create a new Canvas object for printing
                Log.d("Hello Working", "Helooooo");
                PdfDocument pdfDocument = new PrintedPdfDocument(TestPrinterActivity.this, attributes);
                PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(printerTest.getWidth(), printerTest.getHeight(), 1).create();
                PdfDocument.Page page = pdfDocument.startPage(pageInfo);
                Canvas canvas = page.getCanvas();

                // Draw the card view on the canvas
                printerTest.draw(canvas);

                // Finish the page and write the PDF to the file
                pdfDocument.finishPage(page);
                try {
                    pdfDocument.writeTo(new FileOutputStream(destination.getFileDescriptor()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                pdfDocument.close();

                callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }

            @Override
            public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {
                if (cancellationSignal.isCanceled()) {
                    callback.onLayoutCancelled();
                    return;
                }
                PrintDocumentInfo pdi = new PrintDocumentInfo.Builder("Card Print").setContentType(PrintDocumentInfo.CONTENT_TYPE_PHOTO).build();
                callback.onLayoutFinished(pdi, true);
            }
        };

        printManager.print("Card Print", printAdapter, null);
    }

    public void printViaApi(final Bitmap bitmap, final Response.Listener<String> successListener, final Response.ErrorListener errorListener) {
        RequestQueue requestQueue = Volley.newRequestQueue(TestPrinterActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, printURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (successListener != null) {
                    successListener.onResponse(response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (errorListener != null) {
                    errorListener.onErrorResponse(error);
                }
            }
        }) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                return stream.toByteArray();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "image/png");
                //headers.put("Authorization", "Basic " + Base64.encodeToString((username + ":" + password).getBytes(), Base64.NO_WRAP));
                return headers;
            }
        };
        requestQueue.add(stringRequest);
    }

    void printTest2(){
        PrintAttributes attributes1 = new PrintAttributes.Builder()
                .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                .setResolution(new PrintAttributes.Resolution("print", "Print", 300, 300))
                .setColorMode(PrintAttributes.COLOR_MODE_COLOR)
                .build();

        if (printerSocket != null && !printerSocket.isClosed()) {

            OutputStream outputStream = null;
            try {
                outputStream = printerSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Printer is not connected", Toast.LENGTH_SHORT).show();
                connectBtn.setVisibility(View.VISIBLE);
            }

            // Create a new Canvas object and draw the image and text onto it
            Bitmap bitmap = Bitmap.createBitmap(800, 1000, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            printerTest.draw(canvas);
            // Create a new PrintAttributes object with the desired settings

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            // Write the byte array to the OutputStream
            try {
                outputStream.write(byteArray);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Close the OutputStream and the socket
            try {
                outputStream.close();
                printerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Toast.makeText(this, "Print Done", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Printer is not connected", Toast.LENGTH_SHORT).show();
        }
    }

    void printTest3(){
        if (printerSocket != null && !printerSocket.isClosed()) {
            // Get the OutputStream from the socket
            OutputStream outputStream = null;
            try {
                outputStream = printerSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Printer is not connected", Toast.LENGTH_SHORT).show();
                connectBtn.setVisibility(View.VISIBLE);
                return;
            }

            if (outputStream != null) {
                OutputStream finalOutputStream = outputStream;
                PrintDocumentAdapter printAdapter = new PrintDocumentAdapter() {
                    @Override
                    public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {
                        // Create a new Canvas object for printing
                        Bitmap bitmap = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(bitmap);
                        printerTest.draw(canvas);

                        // Convert the bitmap to a byte array
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] byteArray = stream.toByteArray();

                        // Write the byte array to the OutputStream
                        try {
                            finalOutputStream.write(byteArray);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        // Close the OutputStream and the socket
                        try {
                            finalOutputStream.close();
                            printerSocket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        callback.onWriteFinished(new PageRange[]{new PageRange(0, 0)});
                    }

                    @Override
                    public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {
                        // Create a new document info
                        PrintDocumentInfo info = new PrintDocumentInfo.Builder("my_file_name").setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT).build();
                        callback.onLayoutFinished(info, true);

                    }
                };

                printManager.print("my_document_name", printAdapter, null);
            }
            Toast.makeText(this, "Print Done", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Printer is not connected", Toast.LENGTH_SHORT).show();
        }
    }
}

// Handles USB device discovery
class UsbDiscoveryHandler implements DiscoveryHandler {
    public List<DiscoveredPrinterUsb> printers;

    public UsbDiscoveryHandler() {
        printers = new LinkedList<DiscoveredPrinterUsb>();
    }

    public void foundPrinter(final DiscoveredPrinter printer) {
        printers.add((DiscoveredPrinterUsb) printer);
    }

    public void discoveryFinished() {
    }

    public void discoveryError(String message) {
    }
}

// Handles USB device discovery
class NetworkDiscoveryHandler implements DiscoveryHandler {
    public List<DiscoveredPrinter> printers;

    public NetworkDiscoveryHandler() {
        printers = new LinkedList<DiscoveredPrinter>();
    }

    public void foundPrinter(final DiscoveredPrinter printer) {
        printers.add((DiscoveredPrinterNetwork) printer);
    }

    public void discoveryFinished() {
    }

    public void discoveryError(String message) {
    }
}