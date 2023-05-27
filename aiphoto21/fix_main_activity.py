import os
import re

def fix_main_activity(package_name, manifest_path, class_path):
    # Update the package name in AndroidManifest.xml
    manifest_file_path = os.path.join(manifest_path, 'AndroidManifest.xml')
    with open(manifest_file_path, 'r') as file:
        content = file.read()
        updated_content = re.sub(r'package="([^"]+)"', f'package="{package_name}"', content)

    with open(manifest_file_path, 'w') as file:
        file.write(updated_content)

    # Update the package name in MainActivity.kt
    class_file_path = os.path.join(class_path, 'MainActivity.kt')
    with open(class_file_path, 'r') as file:
        content = file.read()
        updated_content = re.sub(r'package ([^;\n]+)', f'package {package_name}', content)

    with open(class_file_path, 'w') as file:
        file.write(updated_content)

    print(f'Successfully updated package name to "{package_name}"')


# Provide the package name, manifest path, and class path here
package_name = 'com.example.aiphoto21'
manifest_path = r'C:\Users\franz\AndroidStudioProjects\AIPhoto21\app\src\main'
class_path = r'C:\Users\franz\AndroidStudioProjects\AIPhoto21\app\src\main\java\com\example\aiphoto21'

fix_main_activity(package_name, manifest_path, class_path)
