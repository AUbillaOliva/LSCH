package cl.afubillaoliva.lsch.utils;

import android.content.Context;

import org.acra.config.CoreConfiguration;
import org.acra.sender.ReportSender;
import org.acra.sender.ReportSenderFactory;

import androidx.annotation.NonNull;

public class Sender implements org.acra.sender.ReportSenderFactory {
    @NonNull
    @Override
    public ReportSender create(@NonNull Context context, @NonNull CoreConfiguration config) {
        return null;
    }

    @Override
    public boolean enabled(@NonNull CoreConfiguration config) {
        return false;
    }

    // NB requires a no arg constructor.

}