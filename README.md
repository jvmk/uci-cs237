# CS237-S2018-ClassProject

This repository contains the code for the "Edge Computing in TIPPERS" project. The project was carried out as a class project for UCI CS237 S2018. The goal of the project is to reduce the strain on the TIPPERS backend by filtering redundant sensor samples at a computational device at the edge of the network (i.e., in proximity of the sensor). The provided code is a generic framework that allows developers to easily implement sensor sample filtering for any arbitrary sensor. We include a sample implementation for surveillance cameras which discards an image if it is logically identical to the previous image (i.e., if the same set of objects are present, in the same order, in both images).

## Project Members
* Janus Varmarken <jvarmark@uci.edu> (lead developer)
* Victor Hsiao <vwhsiao@uci.edu>
* Nishanth Devarajan <devarajn@uci.edu>

## Required Setup
First install Darknet/YOLOv3, download the pretrained model, and put the pretrained model in the proper folder as described here: https://pjreddie.com/darknet/yolo/

Next add a file, ``cameraconfig.properties``, in the ``TippersEdgeFilter/src/main/resources/cfg`` directory with the following contents (specify appropriate values for each key):

    cameraUrl=URL_to_camera_you_want_to_sample_goes_here
    cameraUsername=username_for_logging_in_to_camera_goes_here
    cameraPassword=password_for_logging_in_to_camera_goes_here
    cameraOutputDir=local_path_where_images_downloaded_from_camera_should_be_stored_goes_here

Then add a file, ``darknetconfig.properties``, in the ``TippersEdgeFilter/src/main/resources/cfg`` directory with the following contents (specify appropriate values for each key):

    darknetDir=path_to_where_darknet_is_installed
    
## Executing the program
You should be able to invoke ``./gradlew build`` and then ``./gradlew run`` from the ``TippersEdgeFilter`` directory. This should build (and automatically download all necessary dependencies) and then execute the program.
