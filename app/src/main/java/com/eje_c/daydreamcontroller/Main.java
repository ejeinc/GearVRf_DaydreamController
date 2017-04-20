package com.eje_c.daydreamcontroller;

import com.eje_c.daydreamcontroller.arm_model.ArmModel;
import com.eje_c.daydreamcontroller.arm_model.UpdateDataFactory;
import com.google.vr.sdk.controller.Controller;
import com.google.vr.sdk.controller.ControllerManager;

import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRDrawFrameListener;
import org.gearvrf.GVRMain;
import org.gearvrf.GVRMeshCollider;
import org.gearvrf.GVRPicker;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.scene_objects.GVRModelSceneObject;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.io.IOException;

class Main extends GVRMain {
    private ControllerManager controllerManager;
    private GVRSceneObject controllerModel; // loaded from controller/vr_controller_daydream.obj
    private GVRSceneObject pointTarget;
    private final ArmModel armModel = new ArmModel();
    private final GVRDrawFrameListener controllerDrawer = new GVRDrawFrameListener() {
        @Override
        public void onDrawFrame(float frameTime) {

            // Update controller sensor data
            Controller controller = controllerManager.getController();
            controller.update();

            // Update controller model position
            ArmModel.UpdateData updateData = UpdateDataFactory.create(getGVRContext(), controller, frameTime);
            armModel.Update(updateData);

            Vector3f pos = armModel.getControllerPosition();
            controllerModel.getTransform().setPosition(pos.x(), pos.y(), pos.z());

            Quaternionf rot = armModel.getControllerRotation();
            controllerModel.getTransform().setRotation(rot.w(), rot.x(), rot.y(), rot.z());


            // Pick object with controller
            GVRPicker.GVRPickedObject[] pickedObjects = GVRPicker.pickObjects(getGVRContext().getMainScene(), controllerModel.getTransform(), 0, 0, 0, 0, 0, -1);
            boolean hitWithTestObject = false;
            for (GVRPicker.GVRPickedObject pickedObject : pickedObjects) {
                if (pickedObject.hitObject == pointTarget) {
                    hitWithTestObject = true;
                }
            }

            // Change object opacity
            if (hitWithTestObject) {
                pointTarget.getRenderData().getMaterial().setOpacity(0.5f);
            } else {
                pointTarget.getRenderData().getMaterial().setOpacity(1f);
            }
        }
    };

    @Override
    public void onInit(final GVRContext gvr) throws Throwable {

        GVRScene scene = gvr.getMainScene();

        // Put point target in scene
        pointTarget = new GVRSceneObject(gvr, 3.00f, 2.73f, gvr.getAssetLoader().loadTexture(new GVRAndroidResource(gvr, "gearvr_logo.jpg")));
        pointTarget.getTransform().setPosition(0, 1, -10);
        pointTarget.attachCollider(new GVRMeshCollider(gvr, false));
        scene.addSceneObject(pointTarget);

        // Put controller in scene
        controllerModel = createModel();
        scene.addSceneObject(controllerModel);

        // Get Daydream controller
        controllerManager = new ControllerManager(gvr.getContext(), new ControllerManager.EventListener() {
            @Override
            public void onApiStatusChanged(int apiStatus) {
                switch (apiStatus) {
                    case ControllerManager.ApiStatus.OK: {
                        gvr.registerDrawFrameListener(controllerDrawer);
                        break;
                    }
                    // Error
                    default: {
                        gvr.unregisterDrawFrameListener(controllerDrawer);
                        break;
                    }
                }
            }

            @Override
            public void onRecentered() {
            }
        });
        controllerManager.start();
    }

    private GVRSceneObject createModel() {
        try {
            // Load controller 3D model
            GVRModelSceneObject model = getGVRContext().getAssetLoader().loadModel("controller/vr_controller_daydream.obj", getGVRContext().getMainScene());

            // Put laser pointer at head of controller model
            GVRSceneObject laser = new GVRSceneObject(getGVRContext(), 0.005f, 10.0f, getGVRContext().getAssetLoader().loadTexture(new GVRAndroidResource(getGVRContext(), "controller/laser_texture.png")));
            laser.getTransform().rotateByAxis(-90, 1, 0, 0);
            laser.getTransform().setPosition(0, 0, -5f);
            model.addChildObject(laser);

            return model;

        } catch (IOException e) {
            e.printStackTrace();
            // Empty model
            return new GVRSceneObject(getGVRContext());
        }
    }
}
