package edu.bk.thesis.biodiary.core.voice;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;

import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

import edu.cmu.sphinx.frontend.FrontEnd;
import edu.cmu.sphinx.tools.feature.FeatureFileDumper;
import edu.cmu.sphinx.util.props.ConfigurationManager;


public class SoundFeature
{
    private static final int    RECORDER_BPP                = 16;
    private static final String AUDIO_RECORDER_FILE_EXT_WAV = ".wav";
    private static final String AUDIO_RECORDER_FOLDER       = "AudioRecorder";
    private static final String AUDIO_RECORDER_TEMP_FILE    = "record_temp.raw";
    private static final int    RECORDER_SAMPLERATE         = 16000;
    private static final int    RECORDER_CHANNELS           = AudioFormat.CHANNEL_IN_MONO;
    private static final int    RECORDER_AUDIO_ENCODING     = AudioFormat.ENCODING_PCM_16BIT;
    private static FeatureFileDumper sample;
    private static FeatureFileDumper unlocker;
    private static String               configFile = Environment.getExternalStorageDirectory() +
                                                     "/config.xml";
    private static ConfigurationManager cm         = new ConfigurationManager(configFile);
    private static FrontEnd             frontend   = (FrontEnd) cm.lookup("mfcFrontEnd");
    private static MediaRecorder        mRecorder  = null;
    private static int                  bufferSize = AudioRecord.getMinBufferSize
            (RECORDER_SAMPLERATE, RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING) * 3;


    private static AudioRecord recorder        = null;
    private static Thread      recordingThread = null;
    private static boolean     isRecording     = false;

    public static double main(String[] args) throws Exception
    {
        System.out.println("prepare");

//        Thread.sleep(2000);
//        getOwner();
        return run();
    }

    public static double run()
    {
        try {
            System.out.println("prepare");
//            Thread.sleep(2000);
//            getVoice();
            return compare();

        }
        catch (Exception e) {
            e.printStackTrace();
            return 0;
        }

    }

    public static double compare() throws Exception
    {
        double result = 0;
        getSample();
        getUnlocker();
        double[] sampleVoice   = ffdToArray(sample);
        double[] unlockerVoice = ffdToArray(unlocker);
        result = getScore(sampleVoice, unlockerVoice);
        System.out.println("the result is: " + result);
        return result;
    }

    public static void getSample() throws Exception
    {
        sample = new FeatureFileDumper(cm, "mfcFrontEnd");
        sample.processFile(Environment.getExternalStorageDirectory() + "/AudioRecorder/owner.wav");
    }

    public static void getUnlocker() throws Exception
    {
        unlocker = new FeatureFileDumper(cm, "mfcFrontEnd");
        unlocker.processFile(
                Environment.getExternalStorageDirectory() + "/AudioRecorder/unlocker.wav");
    }

    public static double[] ffdToArray(FeatureFileDumper ffd) throws Exception
    {
        Field field1 = ffd.getClass().getDeclaredField("allFeatures");
        field1.setAccessible(true);
        List<float[]> allFeatures = (List<float[]>) field1.get(ffd);

        double kq[] = new double[allFeatures.size() * 13];
        for (int i = 0; i < allFeatures.size(); i++) {
            for (int j = 0; j < 13; j++) {
                kq[13 * i + j] = (double) (allFeatures.get(i)[j]);
            }
        }
        return kq;
    }

    public static Instance convertSoundtoInstance(double[] array)
    {
        return new DenseInstance(array);
    }

    public static double getScore(double[] sampleVoice, double[] unlockerVoice)
    {
        DTWcustom dtw = new DTWcustom();
        return dtw.measure(convertSoundtoInstance(sampleVoice),
                           convertSoundtoInstance(unlockerVoice));

    }

    public static void getOwner() throws Exception
    {
        System.out.println("begin record");
        startRecording();
        Thread.sleep(5000);
        stopOwnerRecording();
    }

    public static void getVoice() throws Exception
    {
        System.out.println("begin record");
        startRecording();
        Thread.sleep(5000);
        stopUnlockerRecording();
    }

    private static String getOwnerFilename()
    {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File   file     = new File(filepath, AUDIO_RECORDER_FOLDER);

        if (!file.exists()) {
            file.mkdirs();
        }

        return (file.getAbsolutePath() + "/" + "owner" + AUDIO_RECORDER_FILE_EXT_WAV);
    }

    private static String getUnlockerFilename()
    {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File   file     = new File(filepath, AUDIO_RECORDER_FOLDER);

        if (!file.exists()) {
            file.mkdirs();
        }

        return (file.getAbsolutePath() + "/" + "unlocker" + AUDIO_RECORDER_FILE_EXT_WAV);
    }

    private static String getTempFilename()
    {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File   file     = new File(filepath, AUDIO_RECORDER_FOLDER);

        if (!file.exists()) {
            file.mkdirs();
        }

        File tempFile = new File(filepath, AUDIO_RECORDER_TEMP_FILE);

        if (tempFile.exists()) {
            tempFile.delete();
        }

        return (file.getAbsolutePath() + "/" + AUDIO_RECORDER_TEMP_FILE);
    }

    private static void startRecording()
    {
        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                                   RECORDER_SAMPLERATE,
                                   RECORDER_CHANNELS,
                                   RECORDER_AUDIO_ENCODING,
                                   bufferSize);

        int i = recorder.getState();
        if (i == 1) {
            recorder.startRecording();
        }

        isRecording = true;

        Thread recordingThread = new Thread(new Runnable()
        {

            @Override
            public void run()
            {
                writeAudioDataToFile();
            }
        }, "AudioRecorder Thread");

        recordingThread.start();
    }

    private static void writeAudioDataToFile()
    {
        byte             data[]   = new byte[bufferSize];
        String           filename = getTempFilename();
        FileOutputStream os       = null;

        try {
            os = new FileOutputStream(filename);
        }
        catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        int read = 0;

        if (null != os) {
            while (isRecording) {
                read = recorder.read(data, 0, bufferSize);

                if (AudioRecord.ERROR_INVALID_OPERATION != read) {
                    try {
                        os.write(data);
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            try {
                os.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void stopOwnerRecording()
    {
        if (null != recorder) {
            isRecording = false;

            int i = recorder.getState();
            if (i == 1) {
                recorder.stop();
            }
            recorder.release();

            recorder = null;
            recordingThread = null;
        }

        System.out.println("Creating unlocker.wav file");
        copyWaveFile(getTempFilename(), getOwnerFilename());
        System.out.println("Created unlocker.wav file");
        System.out.println("Deleting temp files");
        deleteTempFile();
        System.out.println("Deleted temp files");
    }

    private static void stopUnlockerRecording()
    {
        if (null != recorder) {
            isRecording = false;

            int i = recorder.getState();
            if (i == 1) {
                recorder.stop();
            }
            recorder.release();

            recorder = null;
            recordingThread = null;
        }
        System.out.println("Creating unlocker.wav file");
        copyWaveFile(getTempFilename(), getUnlockerFilename());
        System.out.println("Created unlocker.wav file");
        System.out.println("Deleting temp files");
        deleteTempFile();
        System.out.println("Deleted temp files");
    }


    private static void deleteTempFile()
    {
        File file = new File(getTempFilename());

        file.delete();
    }

    private static void copyWaveFile(String inFilename, String outFilename)
    {
        FileInputStream  in             = null;
        FileOutputStream out            = null;
        long             totalAudioLen  = 0;
        long             totalDataLen   = totalAudioLen + 36;
        long             longSampleRate = RECORDER_SAMPLERATE;
        int              channels       = 1;
        long             byteRate       = RECORDER_BPP * RECORDER_SAMPLERATE * channels / 8;

        byte[] data = new byte[bufferSize];

        try {
            in = new FileInputStream(inFilename);
            out = new FileOutputStream(outFilename);
            totalAudioLen = in.getChannel().size();
            totalDataLen = totalAudioLen + 36;

            WriteWaveFileHeader(out, totalAudioLen, totalDataLen,
                                longSampleRate, channels, byteRate);

            while (in.read(data) != -1) {
                out.write(data);
            }

            in.close();
            out.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void WriteWaveFileHeader(
            FileOutputStream out, long totalAudioLen,
            long totalDataLen, long longSampleRate, int channels,
            long byteRate) throws IOException
    {

        byte[] header = new byte[44];

        header[0] = 'R'; // RIFF/WAVE header
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f'; // 'fmt ' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16; // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1; // format = 1
        header[21] = 0;
        header[22] = (byte) channels;
        header[23] = 0;
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) (2 * 16 / 8); // block align
        header[33] = 0;
        header[34] = RECORDER_BPP; // bits per sample
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);

        out.write(header, 0, 44);
    }
}
