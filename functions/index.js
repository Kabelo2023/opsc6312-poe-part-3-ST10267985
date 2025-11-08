const functions = require("firebase-functions");
const admin = require("firebase-admin");
const express = require("express");
const cors = require("cors");

admin.initializeApp();
const db = admin.firestore();

const app = express();
app.use(cors({ origin: true }));
app.use(express.json());

// Require Firebase ID token
app.use(async (req, res, next) => {
  const hdr = req.headers.authorization || "";
  const token = hdr.startsWith("Bearer ") ? hdr.slice(7) : null;
  if (!token) return res.status(401).json({ message: "Missing token" });
  try {
    req.user = await admin.auth().verifyIdToken(token);
    next();
  } catch {
    res.status(401).json({ message: "Invalid token" });
  }
});

app.get("/events", async (req, res) => {
  const snap = await db.collection("events")
    .where("createdBy", "==", req.user.uid).get();
  res.json(snap.docs.map(d => ({ id: d.id, ...d.data() })));
});

app.post("/events", async (req, res) => {
  const body = req.body || {};
  const ref = await db.collection("events").add({
    title: body.title || "Untitled",
    startIso: body.startIso || new Date().toISOString(),
    endIso: body.endIso || new Date().toISOString(),
    location: body.location || "",
    createdBy: req.user.uid
  });
  const saved = await ref.get();
  res.json({ id: ref.id, ...saved.data() });
});

app.post("/availability/suggest", async (_req, res) => {
  const now = Date.now();
  res.json({ slots: [1, 2, 3].map(h => new Date(now + h * 3600000).toISOString()) });
});

// Use your Firestore region
exports.api = functions.region("africa-south1").https.onRequest(app);
