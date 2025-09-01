import rclpy
from rclpy.node import Node
import serial

from std_msgs.msg import String  # You can change this if using custom msg types

class MotorCommandNode(Node):
    def __init__(self):
        super().__init__('motor_command_node')

        # Adjust the port name if necessary (e.g., /dev/ttyUSB0, /dev/ttyACM0)
        try:
            self.serial_port = serial.Serial('/dev/ttyACM0', 9600, timeout=1)
            self.get_logger().info('Serial connection established on /dev/ttyUSB0')
        except serial.SerialException as e:
            self.get_logger().error(f"Failed to connect to serial device: {e}")
            raise e

        self.subscription = self.create_subscription(
            String,
            'motor_command',
            self.listener_callback,
            10
        )

    def listener_callback(self, msg):
        command = msg.data.strip().lower()

        if command in ['f', 'b', 'l', 'r', 's']:
            self.serial_port.write(command.encode())
            self.get_logger().info(f'Sent command to Arduino: {command}')
        else:
            self.get_logger().warn(f'Invalid command received: {command}')

def main(args=None):
    rclpy.init(args=args)
    node = MotorCommandNode()
    try:
        rclpy.spin(node)
    except KeyboardInterrupt:
        pass
    finally:
        node.destroy_node()
        rclpy.shutdown()

if __name__ == '__main__':
    main()
