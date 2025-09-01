from setuptools import find_packages, setup

package_name = 'aida_motor_controller'

setup(
    name=package_name,
    version='0.0.0',
    packages=find_packages(exclude=['test']),
    data_files=[
        ('share/ament_index/resource_index/packages',
            ['resource/' + package_name]),
        ('share/' + package_name, ['package.xml']),
    ],
    install_requires=['setuptools'],
    zip_safe=True,
    maintainer='karl',
    maintainer_email='karl@todo.todo',
    description='motor controller',
    license='MIT',
    tests_require=['pytest'],
    entry_points={
        'console_scripts': [
		'motor_command_node = aida_motor_controller.motor_command_node:main',
        ],
    },
)
