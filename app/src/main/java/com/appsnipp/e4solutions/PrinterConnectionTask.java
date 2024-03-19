//package com.appsnipp.e4solutions;
//
//import android.content.Context;
//import android.graphics.Bitmap;
//import android.graphics.Canvas;
//import android.os.AsyncTask;
//
//import com.zebra.sdk.comm.Connection;
//import com.zebra.sdk.comm.ConnectionException;
//import com.zebra.sdk.printer.ZebraPrinter;
//import com.zebra.sdk.printer.ZebraPrinterFactory;
//import com.zebra.sdk.printer.ZebraPrinterLanguageUnknownException;
//import com.zebra.sdk.printer.discovery.DiscoveredPrinter;
//import com.zebra.sdk.printer.discovery.NetworkDiscoverer;
//
//import java.io.ByteArrayOutputStream;
//import java.util.ArrayList;
//import java.util.List;
//
//public class PrinterConnectionTask extends AsyncTask<Void, Void, Boolean> {
//
//    private Context mContext;
//    private String mIpAddress;
//    private int mPort;
//    private byte[] mGraphicsData;
//
//    public PrinterConnectionTask(Context context, String ipAddress, int port, byte[] graphicsData) {
//        mContext = context;
//        mIpAddress = ipAddress;
//        mPort = port;
//        mGraphicsData = graphicsData;
//    }
//
//    @Override
//    protected Boolean doInBackground(Void... voids) {
//        try {
//            List<DiscoveredPrinter> printers = new ArrayList<>();
//            NetworkDiscoverer.findPrinters(mContext, printers);
//
//            // Search for the printer with the matching IP address
//            for (DiscoveredPrinter printer : printers) {
//                 if (printer.address.equals(mIpAddress)) {
//                    // Connect to the printer
//                    Connection connection = printer.getConnection();
//                    connection.open();
//                    // Create a Zebra printer object
//                    ZebraPrinter printerObj = ZebraPrinterFactory.getInstance(connection);
//                    // Check if the printer supports the ZPL language
//                    if (printerObj.getPrinterControlLanguage().equals("ZPL")) {
//                        // Send the graphics data to the printer
//                        connection.write(mGraphicsData);
//                        connection.close();
//
//                        // Close the connection to the printer
//                        connection.close();
//
//                        return true;
//                    } else {
//                        // Unsupported printer language
//                        return false;
//                    }
//                }
//            }
//
//            // Printer not found
//            return false;
//        } catch (ConnectionException | ZebraPrinterLanguageUnknownException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//}