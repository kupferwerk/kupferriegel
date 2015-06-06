package com.kupferwerk.kupferriegel.device;

import io.relayr.model.DeviceModel;
import io.relayr.model.Reading;

public class ReadingInfo {
   private DeviceModel deviceModel;
   private Reading reading;

   public DeviceModel getDeviceModel() {
      return deviceModel;
   }

   public void setDeviceModel(DeviceModel deviceModel) {
      this.deviceModel = deviceModel;
   }

   public Reading getReading() {
      return reading;
   }

   public void setReading(Reading reading) {
      this.reading = reading;
   }
}
