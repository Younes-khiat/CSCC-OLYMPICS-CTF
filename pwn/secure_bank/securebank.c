#include <stdio.h>
#include <string.h>

// Function to clear the input buffer
void clear_input_buffer() {
    int c;
    while ((c = getchar()) != '\n' && c != EOF); 
}

// Function to display the secret account (flag)
void secret_account() {
    FILE *file = fopen("flag.txt", "r"); 
    if (file == NULL) {
        printf("\nError: Could not open flag.txt\n");
        return;
    }

    char flag[100];
    if (fgets(flag, sizeof(flag), file) == NULL) { 
        printf("\nError: Could not read flag.txt\n");
        fclose(file);
        return;
    }

    fclose(file); 

    printf("\n========================================\n");
    printf("  Access Granted to Secret Account!\n");
    printf("  Account Balance: $1,000,000\n");
    printf("  Flag: %s", flag);
    printf("========================================\n");
}

void enter_username() {
    char username[64]; 

    printf("\nEnter your username: ");
    gets(username); 
    printf("Hello, %s! You are now logged in.\n", username);
}

void view_balance() {
    printf("\nYour current balance is: $1,000\n");
}

void transfer_funds() {
    printf("\nTransfer funds to another account.\n");
    printf("Transfer successful!\n");
}

void add_balance() {
    int amount;
    printf("\nEnter the amount to add: $");
    scanf("%d", &amount);
    clear_input_buffer(); 
    printf("$%d has been added to your account.\n", amount);
}

void main_menu() {
    int choice;

    while (1) {
        printf("\n========================================\n");
        printf("        Welcome to SecureBank\n");
        printf("========================================\n");
        printf("1. Enter Username\n");
        printf("2. View Balance\n");
        printf("3. Transfer Funds\n");
        printf("4. Add Balance\n");
        printf("5. Exit\n");
        printf("Enter your choice: ");
        scanf("%d", &choice);
        clear_input_buffer(); 

        switch (choice) {
            case 1:
                enter_username();
                break;
            case 2:
                view_balance();
                break;
            case 3:
                transfer_funds();
                break;
            case 4:
                add_balance();
                break;
            case 5:
                printf("\nThank you for banking with us. Have a great day!\n");
                return;
            default:
                printf("\nInvalid choice. Please try again.\n");
        }
    }
}

int main() {
    main_menu();
    return 0;
}