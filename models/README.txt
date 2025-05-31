DOWNLOADING THE SAM MODEL
======================

To use this application, you need to download the Segment Anything Model (SAM) weights.

1. Download the SAM ViT-H model weights from Meta AI:
   https://github.com/facebookresearch/segment-anything#model-checkpoints

2. Save the file "sam_vit_h_4b8939.pth" in this directory.

3. Ensure the path in your application.properties file points to this location:
   app.sam-model.path=${user.dir}/models/sam_vit_h_4b8939.pth

Note: The SAM model file is large (around 2.4GB) and is not included in the repository. 