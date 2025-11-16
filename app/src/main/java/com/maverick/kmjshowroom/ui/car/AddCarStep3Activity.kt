package com.maverick.kmjshowroom.ui.car

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.maverick.kmjshowroom.R
import com.maverick.kmjshowroom.databinding.AddCarstep3Binding

class AddCarStep3Activity : AppCompatActivity() {

    private lateinit var binding: AddCarstep3Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AddCarstep3Binding.inflate(layoutInflater)
        setContentView(binding.root)

        setupHeader()
        setupButtons()
        setupProgressIndicator()
        getSelectedFeatures()
    }

    private fun setupHeader() {
        binding.layoutHeaderadd.iconClose.setOnClickListener {
            finish()
        }
    }

    private fun setupButtons() {
        binding.footerSave3.btnNext.setOnClickListener {
            val intent = Intent(this, AddCarStep4Activity::class.java)
            startActivity(intent)
        }

        binding.footerSave3.btnDraft.setOnClickListener {
            Toast.makeText(this, "Data disimpan sebagai draft", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun setupProgressIndicator() {
        binding.addNewcar3.step1Icon.setImageResource(R.drawable.ic_check_blue)
        binding.addNewcar3.step2Icon.setImageResource(R.drawable.ic_check_blue)
        binding.addNewcar3.step3Icon.setImageResource(R.drawable.ic_number3_blue)
    }

    private fun getSelectedFeatures(): List<String> {
        val selected = mutableListOf<String>()

        if (binding.cbAirbagPengemudi.isChecked) selected.add("Airbag Pengemudi")
        if (binding.cbTractionControl.isChecked) selected.add("Traction Control")
        if (binding.cbBlindSpot.isChecked) selected.add("Blind Spot Monitoring")
        if (binding.cbForwardCollision.isChecked) selected.add("Forward Collision Warning")
        if (binding.cbRearCamera.isChecked) selected.add("Rearview Camera")
        if (binding.cbAbs.isChecked) selected.add("ABS (Anti-lock Braking System)")
        if (binding.cbEsc.isChecked) selected.add("ESC (Electronic Stability Control)")
        if (binding.cbLaneDeparture.isChecked) selected.add("Lane Departure Warning")
        if (binding.cbEmergencyBraking.isChecked) selected.add("Emergency Braking")
        if (binding.cbParkingSensors.isChecked) selected.add("Parking Sensors")

        if (binding.cbAc.isChecked) selected.add("Air Conditioning")
        if (binding.cbPowerSteering.isChecked) selected.add("Power Steering")
        if (binding.cbCentralLocking.isChecked) selected.add("Central Locking")
        if (binding.cbBluetooth.isChecked) selected.add("Bluetooth")
        if (binding.cbAudioSystem.isChecked) selected.add("Premium Audio System")
        if (binding.cbHeatedSeats.isChecked) selected.add("Heated Seats")
        if (binding.cbClimateControl.isChecked) selected.add("Climate Control")
        if (binding.cbPowerWindows.isChecked) selected.add("Power Windows")
        if (binding.cbUsbPort.isChecked) selected.add("USB Port")
        if (binding.cbWirelessCharging.isChecked) selected.add("Wireless Charging")
        if (binding.cbNavigationSeats.isChecked) selected.add("Navigation Seats")
        if (binding.cbVentilatedSeats.isChecked) selected.add("Ventilated Seats")

        if (binding.cbLedHeadlights.isChecked) selected.add("LED Headlights")
        if (binding.cbFogLamps.isChecked) selected.add("Fog Lamps")
        if (binding.cbPanoramicRoof.isChecked) selected.add("Panoramic Roof")
        if (binding.cbRoofRails.isChecked) selected.add("Roof Rails")
        if (binding.cbAlloyWheels.isChecked) selected.add("Alloy Wheels")
        if (binding.cbLedTaillights.isChecked) selected.add("LED Taillights")
        if (binding.cbSunroof.isChecked) selected.add("Sunroof")
        if (binding.cbSpoiler.isChecked) selected.add("Spoiler")
        if (binding.cbChromeTrim.isChecked) selected.add("Chrome Trim")
        if (binding.cbRunflatTires.isChecked) selected.add("Run-flat Tires")

        if (binding.cbEngineImmobilizer.isChecked) selected.add("Engine Immobilizer")
        if (binding.cbPushButtonStart.isChecked) selected.add("Push Button Start")
        if (binding.cbRainSensingWipers.isChecked) selected.add("Rain Sensing Wipers")
        if (binding.cbCruiseControl.isChecked) selected.add("Cruise Control")
        if (binding.cbHillStartAssist.isChecked) selected.add("Hill Start Assist")
        if (binding.cbKeylessEntry.isChecked) selected.add("Keyless Entry")
        if (binding.cbAutoHeadlamps.isChecked) selected.add("Auto Headlamps")
        if (binding.cbParkingAssist.isChecked) selected.add("Parking Assist")
        if (binding.cbAdaptiveCruise.isChecked) selected.add("Adaptive Cruise Control")
        if (binding.cbTirePressure.isChecked) selected.add("Tire Pressure Monitoring")

        return selected
    }
}
