# Algerian Bakery CTF Writeup

## Goal

The goal is to purchase the `VIP Wedding Box` and reveal the flag.

## Intended vulnerability

The bug is an integer overflow in the purchase flow.

In `ShopService.buy()` the code calculates:

```java
int total = quantity * product.getPrice();
```

That multiplication happens in `int`, so a large enough `quantity` wraps around. The wrapped value is then used in the balance check and in the balance deduction.

## Why the exploit works

The user starts with `0` balance. The site gives a `500 DA` bonus. Normally that is nowhere near enough to buy the VIP item priced at `1000 DA`.

But if `quantity * price` overflows into a negative or tiny number, the check:

```java
if (user.getBalance() < total)
```

can be bypassed.

For example, with the `VIP Wedding Box` priced at `1000`, a quantity like `3000000` produces:

```text
3000000 * 1000 = 3000000000
```

That value is larger than the maximum signed 32-bit integer (`2147483647`), so the `int` result wraps around to a negative number instead of staying positive.

After that, the balance update:

```java
user.setBalance(user.getBalance() - total);
```

may increase the balance instead of decreasing it when `total` is negative.

## High-level exploit path

1. Register and log in.
2. Claim the `500 DA` bonus.
3. Submit a large quantity for a product so the multiplication overflows.
4. Use the inflated balance to buy `VIP Wedding Box`.
5. The controller returns the flag after a successful VIP purchase.

## Relevant files

- Purchase logic: `src/main/java/com/example/demo/service/ShopService.java`
- UI and flag return: `src/main/java/com/example/demo/controller/ShopController.java`
- Bonus logic: `src/main/java/com/example/demo/service/UserService.java`
- Product seed data: `src/main/java/com/example/demo/config/DataInitializer.java`

## Notes for solvers

- The app uses session-based auth, not JWT.
- The database is SQLite and is stored as `ctf.db` in the project root.
- The app is intentionally simple; the main intended bug is arithmetic overflow.
