package com.gkpixel.core.managers.cloudnet;

import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.provider.service.SpecificCloudServiceProvider;
import de.dytanic.cloudnet.driver.service.ServiceId;
import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;

public class CloudNetUtils {
    public static boolean hasCloudNet() {
        try {
            Class.forName("de.dytanic.cloudnet.driver.CloudNetDriver");
            return true;
        } catch (ClassNotFoundException e) {
            // CloudNet not exists in jvm
            return false;
        }
    }

    public static String getCurrentServiceName() {
        return CloudNetDriver.getInstance().getComponentName();
    }

    public static ServiceId getCurrentServiceDetails() {
        //getting service name
        String serviceName = CloudNetDriver.getInstance().getComponentName();

        //getting service provider from name
        SpecificCloudServiceProvider provider = CloudNetDriver.getInstance().getCloudServiceProvider(serviceName);
        if (provider == null) return null;

        //getting service info snapshot from service provider
        ServiceInfoSnapshot snapShot = provider.getServiceInfoSnapshot();
        if (snapShot == null) return null;

        //getting service details
        return snapShot.getServiceId();
    }

    public static String getCurrentTaskName() {
        ServiceId serviceDetails = getCurrentServiceDetails();
        if (serviceDetails == null) return null;
        return serviceDetails.getTaskName();
    }
}
