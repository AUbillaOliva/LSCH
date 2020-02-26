package cl.afubillaoliva.lsch;

import android.app.Application;
import android.content.Context;

import org.acra.ACRA;
import org.acra.annotation.AcraCore;
import org.acra.annotation.AcraToast;
import org.acra.config.CoreConfigurationBuilder;
import org.acra.config.SchedulerConfigurationBuilder;

import cl.afubillaoliva.lsch.utils.Sender;

/*
    @Author: Álvaro Felipe Ubilla Oliva
    @Year: 2020
    @Version: v1.0.1
    @License: GPL-3.0
    This app is under the GPL-3.0 license, as well of the LSCH Api
    for more information, see README.md of this repo on Github.
    (https://github.com/AUbillaOliva/LSCH/blob/v1.0.1/README.md)
*/

@AcraCore(reportSenderFactoryClasses = Sender.class,
resReportSendSuccessToast = R.string.report_sended,
resReportSendFailureToast = R.string.acra_toast_text)
@AcraToast(resText = R.string.report_sended)
public class MainApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        CoreConfigurationBuilder builder = new CoreConfigurationBuilder(this)
                .setBuildConfigClass(BuildConfig.class)
                .setEnabled(true);
        builder.getPluginConfigurationBuilder(SchedulerConfigurationBuilder.class)
                .setEnabled(true)
                .setRestartAfterCrash(false);
        ACRA.init(this, builder);
    }
}