# Algerian Bakery CTF (Spring Boot)

Beginner-friendly CTF challenge demonstrating an integer overflow bug in a bakery shop backend.

## Requirements
- Java 26
- Maven 3.9+

## Run

```cmd
mvn spring-boot:run
```

Then open `http://localhost:8080`.

## Challenge Story
You run a traditional Algerian bakery selling:
- Baklawa Tray (300 DA)
- Kalb El Louz Tray (800 DA)
- VIP Wedding Box (1000 DA)

Claim the 500 DA bonus, then exploit the integer overflow in the purchase flow to buy the VIP Wedding Box and reveal the flag.

## Notes
- Session-based authentication; no JWT.
- SQLite database file `ctf.db` is created in the project root.
- The vulnerable logic is in `ShopService.buy()`.