package io.zamzam.basememara.util;

import android.content.res.TypedArray;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import io.zamzam.basememara.App;
import io.zamzam.basememara.R;

/**
 * Created by basem on 7/27/15.
 */
public class ResourceHelper {

    public static int getInteger(int id) {
        return App.getStaticContext()
                .getResources()
                .getInteger(id);
    }

    public static boolean getBoolean(int id) {
        return App.getStaticContext()
                .getResources()
                .getBoolean(id);
    }

    public static List<TypedArray> getMultiTypedArray(String key) {
        List<TypedArray> array = new ArrayList<>();

        try {
            Class<R.array> res = R.array.class;
            Field field;
            int counter = 0;

            do {
                field = res.getField(key + "_" + counter);
                array.add(App.getStaticContext().getResources().obtainTypedArray(field.getInt(null)));
                counter++;
            } while (field != null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return array;
        }
    }

    public static List<Integer> getMultiTypedArrayIds(String key) {
        List<Integer> array = new ArrayList<>();

        try {
            Class<R.array> res = R.array.class;
            Field field;
            int counter = 0;

            do {
                field = res.getField(key + "_" + counter);
                array.add(field.getInt(null));
                counter++;
            } while (field != null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return array;
        }
    }

    public static String getAssetContent(String file) {
        InputStreamReader reader = null;
        InputStream stream = null;
        String output = "";

        try {
            stream = App.getStaticContext().getAssets().open(file);
            reader = new InputStreamReader(stream, Charsets.UTF_8);
            output = CharStreams.toString(reader);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return output;
    }
}