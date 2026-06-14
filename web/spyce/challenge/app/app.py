from flask import Flask, send_from_directory, jsonify, request, abort
import os, sys

BASE_DIR = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, BASE_DIR)
import utils

app = Flask(__name__)

UPLOAD_DIR = os.path.join(BASE_DIR, 'uploads')
TICKETS_DIR = os.path.join(BASE_DIR, 'tickets')
os.makedirs(UPLOAD_DIR, exist_ok=True)
os.makedirs(TICKETS_DIR, exist_ok=True)


@app.route('/')
def index():
    return send_from_directory(os.path.join(BASE_DIR, 'templates'), 'index.html')


@app.route('/app/<path:filename>')
def serve_source(filename):
    base = BASE_DIR
    target = os.path.abspath(os.path.join(base, filename))
    if not target.startswith(base):
        abort(403)
    if not os.path.exists(target):
        abort(404)
    return send_from_directory(os.path.dirname(target), os.path.basename(target))


@app.route('/app/status')
def status():
    return jsonify({
        "service": "Ares Voyages Mission Control",
        "status": "operational",
        "version": "2.4.1",
        "hint": "Source available at /app/app.py"
    })


@app.route('/api/book', methods=['POST'])
def book():
    name    = request.form.get('name', '')
    email   = request.form.get('email', '')
    mission = request.form.get('mission', '')

    if not all([name, email, mission]):
        return jsonify({"error": "Missing required fields"}), 400

    photo = request.files.get('photo')
    if photo:
        filename   = photo.filename
        saved_path = os.path.join(UPLOAD_DIR, filename)
        os.makedirs(os.path.dirname(saved_path), exist_ok=True)
        photo.save(saved_path)

    booking_id = f"ARES-{abs(hash(email)) % 100000:05d}"

    ticket_filename = f"{booking_id}.txt"
    ticket_path = os.path.join(TICKETS_DIR, ticket_filename)
    with open(ticket_path, 'w') as f:
        f.write(f"ARES VOYAGES — BOARDING PASS\n")
        f.write(f"Booking : {booking_id}\n")
        f.write(f"Name    : {name}\n")
        f.write(f"Mission : {mission}\n")
        f.write(f"Status  : CONFIRMED\n")

    return jsonify({
        "booking_id": booking_id,
        "message": "Booking confirmed. Welcome aboard.",
        "ticket": f"/api/ticket/{ticket_filename}"
    })

@app.route('/api/ticket/<path:filename>')
def download_ticket(filename):
    target = os.path.abspath(os.path.join(TICKETS_DIR, filename))

    if not target.startswith(BASE_DIR):
        abort(403)

    if not os.path.exists(target):
        abort(404)

    return send_from_directory(os.path.dirname(target), os.path.basename(target),
                               as_attachment=True)


@app.route('/api/validate-seat')
def validate_seat():
    seat_id = request.args.get('seat_id', '')
    return jsonify({"seat_id": seat_id, "valid": utils.validate_seat(seat_id)})


if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=False)
