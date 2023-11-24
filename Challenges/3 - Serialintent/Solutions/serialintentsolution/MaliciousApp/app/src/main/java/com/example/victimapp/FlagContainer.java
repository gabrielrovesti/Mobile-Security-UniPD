package com.example.victimapp;

import android.util.Base64;
import android.util.Log;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class FlagContainer implements Serializable {
    private final String[] parts;
    private final ArrayList<Integer> perm;

    public FlagContainer(String[] parts, ArrayList<Integer> perm) {
        this.parts = parts;
        this.perm = perm;
    }

    private String getFlag() {
        int n = parts.length;
        int i;
        StringBuilder b64 = new StringBuilder();
        for (i=0; i<n; i++) {
            b64.append(parts[perm.get(i)]);
        }

        byte[] flagBytes = Base64.decode(b64.toString(), Base64.DEFAULT);

        return new String(flagBytes, Charset.defaultCharset());
    }
}