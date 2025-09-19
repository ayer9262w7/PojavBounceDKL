To use minarai on mobile devices use -Dai.djl.default_engine=TFLite

For better Android compatibility with PojavLauncher/Fold Craft Launcher:
- The project now excludes Linux-specific PyTorch native libraries
- TensorFlow Lite is automatically set as the default engine on Android
- Native libraries will be loaded from appropriate Android directories if available
