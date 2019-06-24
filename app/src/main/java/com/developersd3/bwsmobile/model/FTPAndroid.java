package com.developersd3.bwsmobile.model;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import android.os.Environment;
import android.util.Log;

/**
 * Classe responsável por realizar as ações no servidor ftp
 * como criar o diretorio, enviar os arquivos etc.
 * 
 * @author Fred
 *
 */
public class FTPAndroid {

	private FTPClient mFTPClient;
	
	  private static final int BUFFER = 2048; 
	  

	public boolean conectaFTP(String host, String username, String password) {

		boolean status = false;

		try {
			mFTPClient = new FTPClient();

			// Conectando com o Host
			mFTPClient.connect(host, 22);

			// Checa o código de resposta, se for positivo, a conexão foi feita
			if (FTPReply.isPositiveCompletion(mFTPClient.getReplyCode())) {

				// Logando com username e senha
				status = mFTPClient.login(username, password);

				// Setando para o modo de transferência de Arquivos
				mFTPClient.setFileType(FTP.BINARY_FILE_TYPE);
				mFTPClient.enterLocalPassiveMode();
				Log.i("STATUS", "CONECTADO");
				return status;
			}
		} catch (Exception e) {
			Log.i("STATUS", "NÃO CONECTADO");
			Log.i("EXCEPTION FTP", e.getMessage());
		}

		return status;
	}

	public String retornaDiretorioFTP() {
		try {
			String workingDir = mFTPClient.printWorkingDirectory();
			return workingDir;
		} catch (Exception e) {
			Log.i("ERRO", "Erro: Impossível obter o diretorio de trabalho");
		}

		return null;
	}

	public boolean mudarDiretorioFTP(String caminho_dir) {
		try {
		return	mFTPClient.changeWorkingDirectory(caminho_dir);
		} catch (Exception e) {
			Log.i("ERRO", "Erro: Impossível mudar o diretório para "
					+ caminho_dir);
		}
		return false;
	}

	public void imprimeListaArquivosLog(String caminho_dir) {
		try {
			FTPFile[] ftpFiles = mFTPClient.listFiles(caminho_dir);
			int length = ftpFiles.length;

			for (int i = 0; i < length; i++) {
				String name = ftpFiles[i].getName();
				boolean isFile = ftpFiles[i].isFile();

				if (isFile) {
					Log.i("ARQUIVO", "Arquivo : " + name);
				} else {
					Log.i("DIRETORIO", "Diretório: " + name);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


    //ftp nao enviados
    public boolean uploadFTPNaoEnviados(String diretorioAndroid,String arquivoFTP,String diretorioHj) {
        try {
            File file = new File(diretorioAndroid);
            File file2 = new File(diretorioAndroid +"/"+arquivoFTP);
            // verifica se é um diretório
            if (file.isDirectory()) {
                // verifica se há algum arquivo no diretorio
                if (file.list().length > 0) {

                    mFTPClient.enterLocalPassiveMode();
                    mFTPClient.setFileTransferMode(FTPClient.BINARY_FILE_TYPE);
                    mFTPClient.setFileType(FTPClient.BINARY_FILE_TYPE);

                    String diretorioNovo[] = diretorioHj.split("/");

                    // pasta audio
                    if(!mFTPClient.changeWorkingDirectory("/"+diretorioNovo[1])){

                        mFTPClient.makeDirectory("/"+diretorioNovo[1]);
                    }

                    // pasta ano
                    if(!mFTPClient.changeWorkingDirectory("/"+diretorioNovo[1]+"/"+diretorioNovo[2])){

                        mFTPClient.makeDirectory("/"+diretorioNovo[1]+"/"+diretorioNovo[2]);
                    }

                    // pasta mes
                    if(!mFTPClient.changeWorkingDirectory("/"+diretorioNovo[1]+"/"+diretorioNovo[2]+"/"+diretorioNovo[3])){

                        mFTPClient.makeDirectory("/"+diretorioNovo[1]+"/"+diretorioNovo[2]+"/"+diretorioNovo[3]);
                    }

                    // pasta dia
                    if(!mFTPClient.changeWorkingDirectory("/"+diretorioNovo[1]+"/"+diretorioNovo[2]+"/"+diretorioNovo[3]+"/"+diretorioNovo[4])){

                        mFTPClient.makeDirectory("/"+diretorioNovo[1]+"/"+diretorioNovo[2]+"/"+diretorioNovo[3]+"/"+diretorioNovo[4]);
                    }

                    // pasta id
                    if(!mFTPClient.changeWorkingDirectory("/"+diretorioNovo[1]+"/"+diretorioNovo[2]+"/"+diretorioNovo[3]+"/"+diretorioNovo[4]+"/"+diretorioNovo[5])){

                        mFTPClient.makeDirectory("/"+diretorioNovo[1]+"/"+diretorioNovo[2]+"/"+diretorioNovo[3]+"/"+diretorioNovo[4]+"/"+diretorioNovo[5]);
                    }

                    mFTPClient.changeWorkingDirectory("/"+diretorioNovo[1]+"/"+diretorioNovo[2]+"/"+diretorioNovo[3]+"/"+diretorioNovo[4]+"/"+diretorioNovo[5]);

                    FileInputStream arqEnviar = new FileInputStream(diretorioAndroid +"/"+arquivoFTP);

                    // Envio do arquivo
                    if (mFTPClient.storeFile(arquivoFTP, arqEnviar)) {
                        // deleta arquivo do dispositivo

                        String destinationPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DroidCallAudioFiles/audios_enviados/"+arquivoFTP;

                        File sampleDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "/DroidCallAudioFiles/audios_enviados");


                        if (!sampleDir.exists()) {
                            sampleDir.mkdirs();
                        }

                        File destination = new File(destinationPath);

                        copyFile(file2, destination);

                        file2.delete();
                        arqEnviar.close();

                        return true;
                    } else {
                        arqEnviar.close();
                        return false;
                    }
                } else
                    return false;
            } else
                return false;

        } catch (Exception e) {
            return false;
        }
    }




	public boolean uploadFTP(String diretorioAndroid,String arquivoFTP,String diretorioHj) {
		try {
			File file = new File(diretorioAndroid);
			File file2 = new File(diretorioAndroid +"/"+arquivoFTP);
			// verifica se é um diretório
			if (file.isDirectory()) {
				// verifica se há algum arquivo no diretorio
				if (file.list().length > 0) {

					mFTPClient.enterLocalPassiveMode();
					mFTPClient.setFileTransferMode(FTPClient.BINARY_FILE_TYPE);
					mFTPClient.setFileType(FTPClient.BINARY_FILE_TYPE);
					
					String diretorioNovo[] = diretorioHj.split("/");
					
					// pasta audio
					if(!mFTPClient.changeWorkingDirectory("/"+diretorioNovo[1])){
						
						mFTPClient.makeDirectory("/"+diretorioNovo[1]);
					}
					
					// pasta ano
					if(!mFTPClient.changeWorkingDirectory("/"+diretorioNovo[1]+"/"+diretorioNovo[2])){
						
						mFTPClient.makeDirectory("/"+diretorioNovo[1]+"/"+diretorioNovo[2]);
					}
					
					// pasta mes
					if(!mFTPClient.changeWorkingDirectory("/"+diretorioNovo[1]+"/"+diretorioNovo[2]+"/"+diretorioNovo[3])){
						
						mFTPClient.makeDirectory("/"+diretorioNovo[1]+"/"+diretorioNovo[2]+"/"+diretorioNovo[3]);
					}
					
					// pasta dia
					if(!mFTPClient.changeWorkingDirectory("/"+diretorioNovo[1]+"/"+diretorioNovo[2]+"/"+diretorioNovo[3]+"/"+diretorioNovo[4])){
						
						mFTPClient.makeDirectory("/"+diretorioNovo[1]+"/"+diretorioNovo[2]+"/"+diretorioNovo[3]+"/"+diretorioNovo[4]);
					}
					
					// pasta id
					if(!mFTPClient.changeWorkingDirectory("/"+diretorioNovo[1]+"/"+diretorioNovo[2]+"/"+diretorioNovo[3]+"/"+diretorioNovo[4]+"/"+diretorioNovo[5])){
						
						mFTPClient.makeDirectory("/"+diretorioNovo[1]+"/"+diretorioNovo[2]+"/"+diretorioNovo[3]+"/"+diretorioNovo[4]+"/"+diretorioNovo[5]);
					}
				
					mFTPClient.changeWorkingDirectory("/"+diretorioNovo[1]+"/"+diretorioNovo[2]+"/"+diretorioNovo[3]+"/"+diretorioNovo[4]+"/"+diretorioNovo[5]);				
					
					FileInputStream arqEnviar = new FileInputStream(diretorioAndroid +"/"+arquivoFTP);

					// Envio do arquivo
					if (mFTPClient.storeFile(arquivoFTP, arqEnviar)) {
						// deleta arquivo do dispositivo

                        String destinationPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DroidCallAudioFiles/audios_enviados/"+arquivoFTP;

                        File sampleDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "/DroidCallAudioFiles/audios_enviados");


                        if (!sampleDir.exists()) {
                            sampleDir.mkdirs();
                        }

                        File destination = new File(destinationPath);

                        copyFile(file2, destination);

                        file2.delete();
						arqEnviar.close();

						return true;
					} else {
						arqEnviar.close();
						return false;
					}
				} else
					return false;
			} else
				return false;

		} catch (Exception e) {
			return false;
		}
	}

    /*Em Alguns momentos grava o arquivos com sucesso, dando a mensagem "206 transfer complete" e em outros momentos da um erro " 425 Security: Bad IP"

    PROBLEMA RESOLVIDO: JUNIOR FEZ ALTERAÇÕES NO SERVIDOR FTP - vsftpd.conf 'tag pasv_promiscuous = yes'

    */

	public List<File> listarArquivosAEnviar(File diretorio) throws IOException {
			
		List<File> arquivosAEnviar = new ArrayList<File>();
		
		File afile[] = diretorio.listFiles();
		
		int i = 0;
		
		for (int j = afile.length; i < j; i++) {
			
			File arquivo = afile[i];

            if(!arquivo.isDirectory())
            arquivosAEnviar.add(arquivo);

		}

		return arquivosAEnviar;
	}


    public boolean deletarPastaAudioEnviados() throws IOException {

        List<File> arquivosAEnviar = new ArrayList<File>();

        File diretorio = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "/DroidCallAudioFiles/audios_enviados");

        File afile[] = diretorio.listFiles();

        boolean apagados = false;

        int i = 0;

        for (int j = afile.length; i < j; i++) {

            File arquivo = afile[i];

            apagados = arquivo.delete();
        }

        return apagados;

    }
	
	
	public static File retornaArquivoAudioLigacao(File diretorio,String nome) throws IOException {
		
		File retorno = null;
		
		File afile[] = diretorio.listFiles();
		
		int i = 0;
		
		for (int j = afile.length; i < j; i++) {
			
			File arquivo = afile[i];
			
			String nomeArquivo = arquivo.getName();
			
			if(nomeArquivo.equals(nome)){
				retorno = arquivo;
				break;
			}
		}
		
		return retorno;

		
	}
	
	public String[] ziparArquivos(File diretorio) throws IOException {
		
		String afile[] = diretorio.list();
		
		try{		 
			
	     BufferedInputStream origin = null; 
	     
	     File arquivoZipado = new File(diretorio+"zipados.zip");
	     
	     if(!arquivoZipado.isFile())
	    	 arquivoZipado.createNewFile();
	     
	     FileOutputStream dest = new FileOutputStream(arquivoZipado);
	 
	     ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest)); 
	 
	      byte data[] = new byte[BUFFER]; 
	 
	      for(int i=0; i < afile.length; i++) { 
	        Log.v("Compress", "Adding: " + afile[i]); 
	        FileInputStream fi = new FileInputStream(afile[i]); 
	        origin = new BufferedInputStream(fi, BUFFER); 
	        ZipEntry entry = new ZipEntry(afile[i].substring(afile[i].lastIndexOf("/") + 1)); 
	        out.putNextEntry(entry); 
	        int count; 
	        while ((count = origin.read(data, 0, BUFFER)) != -1) { 
	          out.write(data, 0, count); 
	        } 
	        origin.close(); 
	      } 
	 
	      out.close(); 
	      
		}catch(Exception e){
			e.printStackTrace();
		}
				
		return afile;

		
	}

    public static boolean copyFile(File srcFile, File destFile) {
        boolean result = false;
        try {
            InputStream in = new FileInputStream(srcFile);
            try {
                result = copyToFile(in, destFile);
            } finally  {
                in.close();
            }
        } catch (IOException e) {
            result = false;
        }
        return result;
    }
    /**
     * Copy data from a source stream to destFile.
     * Return true if succeed, return false if failed.
     */
    public static boolean copyToFile(InputStream inputStream, File destFile) {
        try {
            if (destFile.exists()) {
                destFile.delete();
            }
            FileOutputStream out = new FileOutputStream(destFile);
            try {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) >= 0) {
                    out.write(buffer, 0, bytesRead);
                }
            } finally {
                out.flush();
                try {
                    out.getFD().sync();
                } catch (IOException e) {
                }
                out.close();
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }
	
}
