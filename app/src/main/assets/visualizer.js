// These are all variables used in the demos.
var noteSequence;
var player, visualizer, viz, vizPLayer;
createSampleSequences();

// Visualizer and player variables
var visualizers = [];
var currentSequence = null;

let currentTime = 0; // Keep track of playback time
let totalDuration = 0; // Total duration of the NoteSequence

let lastNote = noteSequence.notes[noteSequence.notes.length - 1];
let isPlayingLastNote = false
const tolerance = 0.1; // Example: 1 millisecond tolerance

// Player configuration
player = new mm.SoundFontPlayer(
  'https://storage.googleapis.com/magentadata/js/soundfonts/sgm_plus',
  mm.Player.tone.Master,
  null,
  null,
  {
      run: function (note) {

            if (note.startTime >= lastNote.startTime - tolerance) {
                console.log("Playing the last note!");
                isPlayingLastNote = true;
            }

           currentTime = note.startTime;
          // Update visualizers
          for (var i = 0; i < visualizers.length; i++) {
              visualizers[i].redraw(note, true);
          }

          // Highlight current timestamp
          //highlightTimestamp(note.startTime);
      },
      stop: function () {
          for (var i = 0; i < visualizers.length; i++) {
              visualizers[i].clearActiveNotes();
          }

          // Remove highlights when playback stops
          //clearTimestampHighlight();
          checkIfPlaybackComplete()
      },
  }
);

// Function to check if playback is complete
function checkIfPlaybackComplete() {
    if (player.getPlayState() === 'stopped') {

        const reachedEnd = Math.abs(currentTime - totalDuration) < 0.1; // Check if playback reached the end
        if (typeof AndroidBridge !== 'undefined') {
            AndroidBridge.onPlaybackStopped(isPlayingLastNote, currentTime, totalDuration);
        }

        console.log(Math.abs(currentTime - totalDuration))

        if (reachedEnd) {
            console.log('Playback has stopped and reached the maximum time.');
        } else {
            console.log('Playback stopped before reaching the end.');
        }
    }
}

function createSampleSequences() {
  noteSequence = {
    'notes': [
      {'pitch': 60, 'startTime': 0, 'endTime': 0.8333333333333334, 'program': 0, 'clef':'treble'},
      {'pitch': 60, 'startTime': 0.8333333333333334, 'endTime': 1.6666666666666667, 'program': 0, 'clef':'treble'},
      {'pitch': 67, 'startTime': 1.6666666666666667, 'endTime': 2.5, 'program': 0, 'clef':'treble'},
      {'pitch': 67, 'startTime': 2.5, 'endTime': 3.3333333333333335, 'program': 0, 'clef':'treble'},
      {'pitch': 69, 'startTime': 3.3333333333333335, 'endTime': 4.166666666666667, 'program': 0, 'clef':'treble'},
      {'pitch': 69, 'startTime': 4.166666666666667, 'endTime': 5.0, 'program': 0, 'clef':'treble'},
      {'pitch': 67, 'startTime': 5.0, 'endTime': 6.666666666666667, 'program': 0, 'clef':'treble'},
      {'pitch': 65, 'startTime': 6.666666666666667, 'endTime': 7.5, 'program': 0, 'clef':'treble'},
      {'pitch': 65, 'startTime': 7.5, 'endTime': 8.333333333333334, 'program': 0, 'clef':'treble'},
      {'pitch': 64, 'startTime': 8.333333333333334, 'endTime': 9.166666666666668, 'program': 0, 'clef':'treble'},
      {'pitch': 64, 'startTime': 9.166666666666668, 'endTime': 10.000000000000002, 'program': 0, 'clef':'treble'},
      {'pitch': 62, 'startTime': 10.000000000000002, 'endTime': 10.833333333333336, 'program': 0, 'clef':'treble'},
      {'pitch': 62, 'startTime': 10.833333333333336, 'endTime': 11.66666666666667, 'program': 0, 'clef':'treble'},
      {'pitch': 60, 'startTime': 11.66666666666667, 'endTime': 13.333333333333336, 'program': 0, 'clef':'treble'},
      {'pitch': 67, 'startTime': 13.333333333333336, 'endTime': 14.16666666666667, 'program': 0, 'clef':'treble'},
      {'pitch': 67, 'startTime': 14.16666666666667, 'endTime': 15.000000000000004, 'program': 0, 'clef':'treble'},
      {'pitch': 65, 'startTime': 15.000000000000004, 'endTime': 15.833333333333337, 'program': 0, 'clef':'treble'},
      {'pitch': 65, 'startTime': 15.833333333333337, 'endTime': 16.66666666666667, 'program': 0, 'clef':'treble'},
      {'pitch': 64, 'startTime': 16.66666666666667, 'endTime': 17.500000000000004, 'program': 0, 'clef':'treble'},
      {'pitch': 64, 'startTime': 17.500000000000004, 'endTime': 18.333333333333336, 'program': 0, 'clef':'treble'},
      {'pitch': 62, 'startTime': 18.333333333333336, 'endTime': 20.000000000000004, 'program': 0, 'clef':'treble'},
      {'pitch': 67, 'startTime': 20.000000000000004, 'endTime': 20.833333333333336, 'program': 0, 'clef':'treble'},
      {'pitch': 67, 'startTime': 20.833333333333336, 'endTime': 21.666666666666668, 'program': 0, 'clef':'treble'},
      {'pitch': 65, 'startTime': 21.666666666666668, 'endTime': 22.5, 'program': 0, 'clef':'treble'},
      {'pitch': 65, 'startTime': 22.5, 'endTime': 23.333333333333332, 'program': 0, 'clef':'treble'},
      {'pitch': 64, 'startTime': 23.333333333333332, 'endTime': 24.166666666666664, 'program': 0, 'clef':'treble'},
      {'pitch': 64, 'startTime': 24.166666666666664, 'endTime': 24.999999999999996, 'program': 0, 'clef':'treble'},
      {'pitch': 62, 'startTime': 24.999999999999996, 'endTime': 26.666666666666664, 'program': 0, 'clef':'treble'},
      {'pitch': 60, 'startTime': 26.666666666666664, 'endTime': 27.499999999999996, 'program': 0, 'clef':'treble'},
      {'pitch': 60, 'startTime': 27.499999999999996, 'endTime': 28.33333333333333, 'program': 0, 'clef':'treble'},
      {'pitch': 67, 'startTime': 28.33333333333333, 'endTime': 29.16666666666666, 'program': 0, 'clef':'treble'},
      {'pitch': 67, 'startTime': 29.16666666666666, 'endTime': 29.999999999999993, 'program': 0, 'clef':'treble'},
      {'pitch': 69, 'startTime': 29.999999999999993, 'endTime': 30.833333333333325, 'program': 0, 'clef':'treble'},
      {'pitch': 69, 'startTime': 30.833333333333325, 'endTime': 31.666666666666657, 'program': 0, 'clef':'treble'},
      {'pitch': 67, 'startTime': 31.666666666666657, 'endTime': 33.33333333333332, 'program': 0, 'clef':'treble'},
      {'pitch': 65, 'startTime': 33.33333333333332, 'endTime': 34.16666666666666, 'program': 0, 'clef':'treble'},
      {'pitch': 65, 'startTime': 34.16666666666666, 'endTime': 34.99999999999999, 'program': 0, 'clef':'treble'},
      {'pitch': 64, 'startTime': 34.99999999999999, 'endTime': 35.83333333333333, 'program': 0, 'clef':'treble'},
      {'pitch': 64, 'startTime': 35.83333333333333, 'endTime': 36.666666666666664, 'program': 0, 'clef':'treble'},
      {'pitch': 62, 'startTime': 36.666666666666664, 'endTime': 37.5, 'program': 0, 'clef':'treble'},
      {'pitch': 62, 'startTime': 37.5, 'endTime': 38.333333333333336, 'program': 0, 'clef':'treble'},
      {'pitch': 60, 'startTime': 38.333333333333336, 'endTime': 40.0, 'program': 0, 'clef':'treble'},

      {'pitch': 52, 'startTime': 0, 'endTime': 0.8333333333333334, 'program': 0, 'clef':'bass'},
      {'pitch': 52, 'startTime': 0.8333333333333334, 'endTime': 1.6666666666666667, 'program': 0, 'clef':'bass'},
      {'pitch': 52, 'startTime': 1.6666666666666667, 'endTime': 2.5, 'program': 0, 'clef':'bass'},
      {'pitch': 52, 'startTime': 2.5, 'endTime': 3.3333333333333335, 'program': 0, 'clef':'bass'},
      {'pitch': 53, 'startTime': 3.3333333333333335, 'endTime': 4.166666666666667, 'program': 0, 'clef':'bass'},
      {'pitch': 53, 'startTime': 4.166666666666667, 'endTime': 5.0, 'program': 0, 'clef':'bass'},
      {'pitch': 52, 'startTime': 5.0, 'endTime': 6.666666666666667, 'program': 0, 'clef':'bass'},
      {'pitch': 50, 'startTime': 6.666666666666667, 'endTime': 7.5, 'program': 0, 'clef':'bass'},
      {'pitch': 50, 'startTime': 7.5, 'endTime': 8.333333333333334, 'program': 0, 'clef':'bass'},
      {'pitch': 48, 'startTime': 8.333333333333334, 'endTime': 9.166666666666668, 'program': 0, 'clef':'bass'},
      {'pitch': 48, 'startTime': 9.166666666666668, 'endTime': 10.000000000000002, 'program': 0, 'clef':'bass'},
      {'pitch': 47, 'startTime': 10.000000000000002, 'endTime': 10.833333333333336, 'program': 0, 'clef':'bass'},
      {'pitch': 47, 'startTime': 10.833333333333336, 'endTime': 11.66666666666667, 'program': 0, 'clef':'bass'},
      {'pitch': 48, 'startTime': 11.66666666666667, 'endTime': 13.333333333333336, 'program': 0, 'clef':'bass'},
      {'pitch': 52, 'startTime': 13.333333333333336, 'endTime': 14.16666666666667, 'program': 0, 'clef':'bass'},
      {'pitch': 52, 'startTime': 14.16666666666667, 'endTime': 15.000000000000004, 'program': 0, 'clef':'bass'},
      {'pitch': 50, 'startTime': 15.000000000000004, 'endTime': 15.833333333333337, 'program': 0, 'clef':'bass'},
      {'pitch': 50, 'startTime': 15.833333333333337, 'endTime': 16.66666666666667, 'program': 0, 'clef':'bass'},
      {'pitch': 48, 'startTime': 16.66666666666667, 'endTime': 17.500000000000004, 'program': 0, 'clef':'bass'},
      {'pitch': 48, 'startTime': 17.500000000000004, 'endTime': 18.333333333333336, 'program': 0, 'clef':'bass'},
      {'pitch': 47, 'startTime': 18.333333333333336, 'endTime': 20.000000000000004, 'program': 0, 'clef':'bass'},
      {'pitch': 52, 'startTime': 20.000000000000004, 'endTime': 20.833333333333336, 'program': 0, 'clef':'bass'},
      {'pitch': 52, 'startTime': 20.833333333333336, 'endTime': 21.666666666666668, 'program': 0, 'clef':'bass'},
      {'pitch': 50, 'startTime': 21.666666666666668, 'endTime': 22.5, 'program': 0, 'clef':'bass'},
      {'pitch': 50, 'startTime': 22.5, 'endTime': 23.333333333333332, 'program': 0, 'clef':'bass'},
      {'pitch': 48, 'startTime': 23.333333333333332, 'endTime': 24.166666666666664, 'program': 0, 'clef':'bass'},
      {'pitch': 48, 'startTime': 24.166666666666664, 'endTime': 24.999999999999996, 'program': 0, 'clef':'bass'},
      {'pitch': 43, 'startTime': 24.999999999999996, 'endTime': 26.666666666666664, 'program': 0, 'clef':'bass'},
      {'pitch': 52, 'startTime': 26.666666666666664, 'endTime': 27.499999999999996, 'program': 0, 'clef':'bass'},
      {'pitch': 52, 'startTime': 27.499999999999996, 'endTime': 28.33333333333333, 'program': 0, 'clef':'bass'},
      {'pitch': 52, 'startTime': 28.33333333333333, 'endTime': 29.16666666666666, 'program': 0, 'clef':'bass'},
      {'pitch': 52, 'startTime': 29.16666666666666, 'endTime': 29.999999999999993, 'program': 0, 'clef':'bass'},
      {'pitch': 53, 'startTime': 29.999999999999993, 'endTime': 30.416666666666667, 'program': 0, 'clef':'bass'},
      {'pitch': 55, 'startTime': 30.416666666666667, 'endTime': 30.833333333333325, 'program': 0, 'clef':'bass'},
      {'pitch': 57, 'startTime': 30.833333333333325, 'endTime': 31.25, 'program': 0, 'clef':'bass'},
      {'pitch': 59, 'startTime': 31.25, 'endTime': 31.666666666666657, 'program': 0, 'clef':'bass'},
      {'pitch': 60, 'startTime': 31.666666666666657, 'endTime': 33.33333333333332, 'program': 0, 'clef':'bass'},
      {'pitch': 50, 'startTime': 33.33333333333332, 'endTime': 34.16666666666666, 'program': 0, 'clef':'bass'},
      {'pitch': 57, 'startTime': 34.16666666666666, 'endTime': 34.99999999999999, 'program': 0, 'clef':'bass'},
      {'pitch': 55, 'startTime': 34.99999999999999, 'endTime': 35.83333333333333, 'program': 0, 'clef':'bass'},
      {'pitch': 48, 'startTime': 35.83333333333333, 'endTime': 36.666666666666664, 'program': 0, 'clef':'bass'},
      {'pitch': 55, 'startTime': 36.666666666666664, 'endTime': 37.5, 'program': 0, 'clef':'bass'},
      {'pitch': 43, 'startTime': 37.5, 'endTime': 38.333333333333336, 'program': 0, 'clef':'bass'},
      {'pitch': 48, 'startTime': 38.333333333333336, 'endTime': 40.0, 'program': 0, 'clef':'bass'}],
    'timeSignatures': [{'time': 0, 'numerator': 4, 'denominator': 4}],
    'tempos': [{'time': 0, 'qpm': 72}],
    'totalTime': 40
  };
}

var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
  function adopt(value) { return value instanceof (P || Promise) ? value : new (P || Promise)(function (resolve) { resolve(value); }); }
  return new (P || Promise)(function (resolve, reject) {
      function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
      function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
      function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
      step((generator = generator.apply(thisArg, _arguments || [])).next());
  });
};


function adopt(value) {
  return value instanceof P ? value : new P(function (resolve) { resolve(value); });
}

function __generator(thisArg, body) {
  var _ = { label: 0, sent: function () { if (t[0] & 1) throw t[1]; return t[1]; }, trys: [], ops: [] }, f, y, t, g;
  return g = { next: verb(0), "throw": verb(1), "return": verb(2) }, typeof Symbol === "function" && (g[Symbol.iterator] = function () { return this; }), g;
  function verb(n) { return function (v) { return step([n, v]); }; }
  function step(op) {
      if (f) throw new TypeError("Generator is already executing.");
      while (_) try {
          if (f = 1, y && (t = op[0] & 2 ? y["return"] : op[0] ? y["throw"] || ((t = y["return"]) && t.call(y), 0) : y.next) && !(t = t.call(y, op[1])).done) return t;
          if (y = 0, t) op = [op[0] & 2, t.value];
          switch (op[0]) {
              case 0: case 1: t = op; break;
              case 4: _.label++; return { value: op[1], done: false };
              case 5: _.label++; y = op[1]; op = [0]; continue;
              case 7: op = _.ops.pop(); _.trys.pop(); continue;
              default:
                  if (!(t = _.trys, t = t.length > 0 && t[t.length - 1]) && (op[0] === 6 || op[0] === 2)) { _ = 0; continue; }
                  if (op[0] === 3 && (!t || (op[1] > t[0] && op[1] < t[3]))) { _.label = op[1]; break; }
                  if (op[0] === 6 && _.label < t[1]) { _.label = t[1]; t = op; break; }
                  if (t && _.label < t[2]) { _.label = t[2]; _.ops.push(op); break; }
                  if (t[2]) _.ops.pop();
                  _.trys.pop(); continue;
          }
          op = body.call(thisArg, _);
      } catch (e) { op = [6, e]; y = 0; } finally { f = t = 0; }
      if (op[0] & 5) throw op[1]; return { value: op[0] ? op[1] : void 0, done: true };
  }
}

var __extends = (this && this.__extends) || (function () {
  var extendStatics = function (d, b) {
      extendStatics = Object.setPrototypeOf ||
          ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
          function (d, b) { for (var p in b) if (Object.prototype.hasOwnProperty.call(b, p)) d[p] = b[p]; };
      return extendStatics(d, b);
  };

  return function (d, b) {
      extendStatics(d, b);
      function __() { this.constructor = d; }
      d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
  };
})();

var trebleStaff = document.getElementById('treble-staff');
var bassStaff = document.getElementById('bass-staff');

// Load MIDI file from file input
function loadFile(e) {
    index_1.blobToNoteSequence(e.target.files[0])
        .then(function (seq) {
            initPlayerAndVisualizer(seq);
        }
    );
}


// Initialize the player and StaffSVGVisualizer
function initPlayerAndVisualizer(seq=noteSequence) {
  return __awaiter(this, void 0, void 0, function () {
      var tempo;
      return __generator(this, function (_a) {
          switch (_a.label) {
              case 0:
                noteSequence = seq
                calculateTotalDuration(seq);
                  // Filter notes based on clef
                  const trebleNotes = seq.notes.filter(note => note.clef === "treble");
                  const bassNotes = seq.notes.filter(note => note.clef === "bass");

                  // Create separate note sequences for each clef
                  const trebleSequence = { ...seq, notes: trebleNotes };
                  const bassSequence = { ...seq, notes: bassNotes };

                  visualizers = [
                      new mm.StaffSVGVisualizer(trebleSequence, trebleStaff, {
                        scrollType: mm.ScrollType.NOTE
                    }),
                      new mm.StaffSVGVisualizer(bassSequence, bassStaff, {
                      scrollType: mm.ScrollType.NOTE
                    }),
                  ];
                  currentSequence = seq;
                  tempo = seq.tempos[0].qpm;

                  // Set tempo
                  player.setTempo(tempo);
                  return [4, player.loadSamples(seq)];
              case 1:
                  _a.sent();
                  return [2];
          }
      });
  });
}





/// Updated function to initialize player and highlight notes
function initPlayerAndVisualizerHighlight(seq = noteSequence, highlightIndices = "") {
    return __awaiter(this, void 0, void 0, function () {
        let tempo;
        return __generator(this, function (_a) {
            switch (_a.label) {
                case 0:
                    const highlightIndicesString = highlightIndices.toString();
                    const highlightIndicesArray = highlightIndicesString.split(",").map(Number);

                    // Add isHighlighted property for each note
                    seq.notes = seq.notes.map((note, index) => ({
                        ...note,
                        isHighlighted: highlightIndicesArray.includes(index),
                    }));

                    noteSequence = seq;
                    calculateTotalDuration(seq);

                    // Separate notes by clef
                    const trebleNotes = seq.notes
                        .map((note, index) => ({ ...note, originalIndex: index }))
                        .filter(note => note.clef === "treble");
                    const bassNotes = seq.notes
                        .map((note, index) => ({ ...note, originalIndex: index }))
                        .filter(note => note.clef === "bass");

                    // Create clef-specific sequences
                    const trebleSequence = { ...seq, notes: trebleNotes };
                    const bassSequence = { ...seq, notes: bassNotes };

                    visualizers = [
                        new mm.StaffSVGVisualizer(trebleSequence, trebleStaff, {
                            scrollType: mm.ScrollType.NOTE,
                        }),
                        new mm.StaffSVGVisualizer(bassSequence, bassStaff, {
                            scrollType: mm.ScrollType.NOTE,
                        }),
                    ];
                    currentSequence = seq;
                    tempo = seq.tempos[0].qpm;

                    // Set tempo
                    player.setTempo(tempo);

                    // Extract highlight indices for treble and bass clefs
                    const trebleHighlightIndices = highlightIndicesArray.filter(index =>
                        trebleNotes.some(note => note.originalIndex === index)
                    ).map(index => trebleNotes.findIndex(note => note.originalIndex === index));

                    const bassHighlightIndices = highlightIndicesArray.filter(index =>
                        bassNotes.some(note => note.originalIndex === index)
                    ).map(index => bassNotes.findIndex(note => note.originalIndex === index));

                    // Log rendered data-ids for debugging
                    function logRenderedDataIds(staff, clef) {
                        const dataIds = Array.from(staff.querySelectorAll("g[data-id]"))
                            .map(element => element.getAttribute("data-id"));
                        console.log(`${clef} Staff Rendered Data IDs:`, dataIds);
                    }

                    function highlightNotes(notes, highlightIndices, staff, clef) {
                        notes.forEach((note, index) => {
                            const dataId = `${index}-${note.pitch}`;
                            const noteElement = staff.querySelector(`g[data-id="${dataId}"]`);

                            if (!noteElement) {
                                console.warn(`${clef} Note element not found for data-id: ${dataId}`);
                            } else if (highlightIndices.includes(index)) {
                                console.log(`${clef} Note ${index} is highlighted with data-id: ${dataId}`);
                                noteElement.style.fill = 'red';
                                noteElement.style.strokeWidth = '2px';
                            } else {
                                console.log(`${clef} Note ${index} is not highlighted with data-id: ${dataId}`);
                                noteElement.style.fill = 'black';
                                noteElement.style.strokeWidth = '1px';
                            }
                        });
                    }

                    return [
                        4,
                        player.loadSamples(seq).then(() => {
                            // Wait for rendering to complete
                            setTimeout(() => {
                                // Log rendered data-ids for debugging
                                logRenderedDataIds(trebleStaff, "Treble");
                                logRenderedDataIds(bassStaff, "Bass");

                                // Highlight notes
                                highlightNotes(trebleNotes, trebleHighlightIndices, trebleStaff, "Treble");
                                highlightNotes(bassNotes, bassHighlightIndices, bassStaff, "Bass");
                            }, 100); // Adjust the delay as needed for rendering
                        }),
                    ];
                case 1:
                    _a.sent();
                    return [2];
            }
        });
    });
}





// Start or Stop the player
function togglePlay() {
  if (player.isPlaying()) {
      player.stop();
  } else {
      isPlayingLastNote = false
      player.start(currentSequence);
  }
}

function start() {
  if (!player.isPlaying()) {
        isPlayingLastNote = false
        player.start(currentSequence);
    }
}

function stop() {
  if (player.isPlaying()) { player.stop(); }
}

// Function to calculate total duration based on the last note's end time
function calculateTotalDuration(noteSequence) {
    if (!noteSequence.notes || noteSequence.notes.length === 0) {
        return 0;
    }
    // Find the maximum `endTime` in the note sequence
    totalDuration = Math.max(...noteSequence.notes.map(note => note.endTime));
    console.log(`Calculated total duration: ${totalDuration} seconds.`);
    return totalDuration;
}

function renderTimestamps(sequence) {
  const timestampsDiv = document.getElementById('timestamps');
  timestampsDiv.innerHTML = ''; // Clear previous timestamps

  const notes = sequence.notes;
  let timestamps = notes.map(note => note.startTime);

  // Remove duplicates by converting timestamps to a Set and back to an array
  timestamps = [...new Set(timestamps)];

  // Sort timestamps in ascending order
  timestamps.sort((a, b) => a - b);

  // Render timestamps rounded to one decimal place
  timestamps.forEach((time) => {
      const timestampEl = document.createElement('div');
      timestampEl.className = 'timestamp';

      // Format timestamp to one decimal place
      const formattedTime = time.toFixed(1); // Round to one decimal place

      timestampEl.textContent = formattedTime;
      timestampsDiv.appendChild(timestampEl);
  });
}

function highlightTimestamp(time) {
  const timestampsDiv = document.getElementById('timestamps');
  const timestamps = timestampsDiv.getElementsByClassName('timestamp');

  // Round time to match formatted timestamp
  const roundedTime = parseFloat(time.toFixed(1)); // Use .toFixed(0) for whole seconds

  // Highlight the matching timestamp
  Array.from(timestamps).forEach((el) => {
      const timestampValue = parseFloat(el.textContent);
      if (timestampValue === roundedTime) {
          el.classList.add('active-timestamp');
          el.scrollIntoView({ behavior: 'smooth', inline: 'center' }); // Keep active timestamp in view
      } else {
          el.classList.remove('active-timestamp');
      }
  });
}

function clearTimestampHighlight() {
  const timestamps = document.getElementsByClassName('timestamp');
  Array.from(timestamps).forEach((el) => el.classList.remove('active-timestamp'));
}