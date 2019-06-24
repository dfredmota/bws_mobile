//package com.developersd3.bwsmobile.tasks;
//
//import android.app.ProgressDialog;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.AsyncTask;
//import android.os.Environment;
//import android.util.Log;
//
//import com.developersd3.bwsmobile.BuildConfig;
//import com.developersd3.bwsmobile.activity.LoginAct;
//import com.jcraft.jsch.Channel;
//import com.jcraft.jsch.ChannelSftp;
//import com.jcraft.jsch.JSch;
//import com.jcraft.jsch.Session;
//
//import java.io.File;
//import java.util.Properties;
//import java.util.Vector;
//
///**
// * Created by fred on 06/11/16.
// */
//
//public class UpdateApkTask extends AsyncTask<Void, Void, Boolean> {
//
//    private String FTP_HOST = "162.243.196.32";
//    private String FTP_USER = "rails";
//    private String FTP_PASS = "bws@1234";
//    private Integer FTP_PORT = 22;
//
//    public UpdateApkTask(){
//        super();
//    }
//
//    private String numeroVersaoAppServidor = null;
//
//    @Override
//    protected Boolean doInBackground(Void... params) {
//
//
//        try{
//            JSch jsch = new JSch();
//
//            Properties config = new Properties();
//            config.put("StrictHostKeyChecking", "no");
//            config.put("compression.s2c", "zlib,none");
//            config.put("compression.c2s", "zlib,none");
//
//            Session session = jsch.getSession(FTP_USER, FTP_HOST, FTP_PORT);
//            session.setPassword(FTP_PASS);
//            session.setConfig(config);
//            session.connect();
//
//            Channel channel =  session.openChannel("sftp");
//            channel.connect();
//
//            ChannelSftp sftp = (ChannelSftp) channel;
//
//            @SuppressWarnings("unchecked")
//            final Vector<ChannelSftp.LsEntry> files = sftp.ls("apk");
//
//            String nomeArquivoApk = null;
//
//            for (ChannelSftp.LsEntry obj : files) {
//                String nomeCompletoArquivo = obj.toString().toLowerCase();
//                if(nomeCompletoArquivo.contains(".apk") && nomeCompletoArquivo.contains("bws")) {
//                    //Split separando por espaço em branco
//                    String split1 [] = nomeCompletoArquivo.split("\\s");
//                    for (int i=0; i<split1.length; i++) {
//                        if(split1[i].contains(".apk") && split1[i].contains("bws")) {
//                            //Exemplo de nome de arquivo válido "BWS_1-0.apk" onde a String '1-0' representa o número da versão 1.0
//                            nomeArquivoApk = split1[i];
//                            System.out.println("Nome do arquivo: " + nomeArquivoApk);
//
//                            numeroVersaoAppServidor = nomeArquivoApk.replace("bws_", "").replace(".apk", "").replace("-", ".");
//                            System.out.println("Numero da versao servidor: " + numeroVersaoAppServidor);
//                        }
//                    }
//                }
//            }
//
//            String numeroVersaoAppLocal = BuildConfig.VERSION_NAME;
//
//            System.out.println("Nome versao local: " + numeroVersaoAppLocal);
//
//            if(nomeArquivoApk != null && (numeroVersaoAppLocal == null || !numeroVersaoAppLocal.equals(numeroVersaoAppServidor))) {
//
//                System.out.println("Situacao Armazenamento: " + Environment.getExternalStorageState());
//
//                //Entrando na pasta /home/rails/apk do SFTP
//                sftp.cd("/home/rails/apk");
//
//                //Path onde ficará sera baixado a nova versao da apk
//                pathApk = Environment.getExternalStorageDirectory() + "/bws.apk";
//
//                System.out.println("Path: " + pathApk);
//
//                //Baixando o arquivo
//                sftp.get(nomeArquivoApk, pathApk);
//
//                //Setando a variável para a apk ser atualizada
//                atualizarVersao = true;
//
//            } else {
//                //Setando a variável para a apk não ser atualizada
//                atualizarVersao = false;
//            }
//
//            channel.disconnect();
//            session.disconnect();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return true;
//    }
//
//    @Override
//    protected void onPostExecute(final Boolean success) {
//
//        mUpdateApkTask = null;
//
//        ringProgressDialog.dismiss();
//
//        // Responsável por executar a instalação do apk se o boolean indicar que a versao é antiga
//        if(atualizarVersao) {
//            System.out.println("Iniciando a atualizacao da app...");
//            Intent i = new Intent();
//            i.setAction(Intent.ACTION_VIEW);
//            i.setDataAndType(Uri.fromFile(new File(pathApk)), "application/vnd.android.package-archive");
//
//            Log.d("Lofting", "About to install new .apk");
//            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(i);
//        }
//    }
//
//    private void verificarNovaAtualizacao() {
//        ringProgressDialog = ProgressDialog.show(LoginAct.this, "Aguarde", "Verificando atualizações...", true);
//        ringProgressDialog.setCancelable(false);
//
//        mUpdateApkTask = new UpdateApkFtp();
//        mUpdateApkTask.execute((Void) null);
//    }
//
//    @Override
//    protected void onCancelled() {
//        mUpdateApkTask = null;
//    }
//}
