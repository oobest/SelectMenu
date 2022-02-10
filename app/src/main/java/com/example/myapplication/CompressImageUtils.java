package com.example.myapplication;

import android.content.Context;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 *
 * @author albert
 * @date 2022/1/11 14:09
 * @mail 416392758@@gmail.com
 * @since v1
 */
public class CompressImageUtils {

    public interface ResultListener {
        void onSuccess(File file);

        void onError(Throwable throwable);
    }

    public static void compress(Context context,
                                String file,
                                File target,
                                ResultListener resultListener) {
        compress(context, new File(file), target, resultListener);
    }

    public static void compress(Context context,
                                File file,
                                File target,
                                ResultListener resultListener) {
        Luban.with(context.getApplicationContext())
                .load(file)
                .ignoreBy(300)
                .setTargetDir(target.getParent())
                .setRenameListener(it -> target.getName())
                .filter(path -> !TextUtils.isEmpty(path))
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onSuccess(File resultFile) {
                        if (!resultFile.getAbsolutePath().equals(file.getAbsolutePath())
                                && resultFile.exists()
                                && resultFile.length() > 0) {
                            // compressed success
                            resultListener.onSuccess(resultFile);
                        } else {
                            try {
                                copyFileUsingFileChannels(file, target);
                                resultListener.onSuccess(target);
                            } catch (IOException e) {
                                e.printStackTrace();
                                resultListener.onError(e);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        resultListener.onError(e);
                    }
                })
                .launch();

    }

    private static void copyFileUsingFileChannels(File source, File dest) throws IOException {
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            inputChannel = new FileInputStream(source).getChannel();
            outputChannel = new FileOutputStream(dest).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        } finally {
            if (inputChannel != null) {
                inputChannel.close();
            }
            if (outputChannel != null) {
                outputChannel.close();
            }
        }
    }
}
