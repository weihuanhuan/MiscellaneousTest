package spring.jdbc.aop.realtime.updated;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UpdatedConfigPusher {

    private final Logger LOGGER = Logger.getLogger(this.getClass().getName());

    public void pushUpdatedConfig(UpdatedConfigRecorder updatedConfigRecorder) {
        if (updatedConfigRecorder == null) {
            return;
        }

        Collection<UpdatedConfig> updatedConfigs = updatedConfigRecorder.getUpdatedConfig();
        if (updatedConfigs == null || updatedConfigs.isEmpty()) {
            return;
        }

        for (UpdatedConfig updatedConfig : updatedConfigs) {
            if (updatedConfig == null) {
                continue;
            }

            try {
                pushUpdatedConfig(updatedConfig);
            } catch (Throwable throwable) {
                String name = updatedConfig.getName();
                String format = String.format("failed to push updated config for [%s] named [%s]!", updatedConfig.getClass()
                        .getSimpleName(), name);
                LOGGER.log(Level.WARNING, format, throwable);
            }
        }
    }

    private void pushUpdatedConfig(UpdatedConfig updatedConfig) {
        String format = String.format("push updated config for [%s].", updatedConfig);
        System.out.println(format);
    }

}
