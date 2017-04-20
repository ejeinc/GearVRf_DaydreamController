package com.eje_c.daydreamcontroller.arm_model;

import com.google.vr.sdk.controller.Controller;

import org.gearvrf.GVRContext;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class UpdateDataFactory {

    private static final Vector3f ZERO = new Vector3f();
    private static final Vector3f FORWARD = new Vector3f(0, 0, -1);
    private static final Vector3f cameraForward = new Vector3f();
    private static final Quaternionf orientation = new Quaternionf();

    /**
     * Create {@link com.eje_c.daydreamcontroller.arm_model.ArmModel.UpdateData} from {@link Controller}.
     */
    public static ArmModel.UpdateData create(GVRContext gvr, Controller controller, float frameTime) {

        // update current forward vector
        gvr.getMainScene().getMainCameraRig().getHeadTransform()
                .getModelMatrix4f().transformPosition(FORWARD, cameraForward);

        // set controller orientation
        orientation.set(controller.orientation.x, controller.orientation.y, controller.orientation.z, controller.orientation.w);

        return new ArmModel.UpdateData(true, ZERO, orientation, ZERO, FORWARD, ZERO, frameTime);
    }
}
