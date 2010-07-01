package org.petero.cuckoochess;

import java.util.ArrayList;
import java.util.List;

import guibase.ChessController;
import guibase.GUIInterface;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.ClipboardManager;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnTouchListener;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import chess.ChessParseError;
import chess.Move;
import chess.Position;

public class CuckooChess extends Activity implements GUIInterface {
	ChessBoard cb;
	ChessController ctrl;
	boolean mShowThinking;
	int mTimeLimit;
	boolean playerWhite;
	static final int ttLogSize = 16; // Use 2^ttLogSize hash entries.
	
	TextView status;
	ScrollView moveListScroll;
	TextView moveList;
	TextView thinking;
	
	SharedPreferences settings;

	private void readPrefs() {
        mShowThinking = settings.getBoolean("showThinking", false);
        String timeLimitStr = settings.getString("timeLimit", "5000");
        mTimeLimit = Integer.parseInt(timeLimitStr);
        playerWhite = settings.getBoolean("playerWhite", true);
        boolean boardFlipped = settings.getBoolean("boardFlipped", false);
        cb.setFlipped(boardFlipped);
        ctrl.setTimeLimit();
        String fontSizeStr = settings.getString("fontSize", "12");
        int fontSize = Integer.parseInt(fontSizeStr);
        status.setTextSize(fontSize);
        moveList.setTextSize(fontSize);
        thinking.setTextSize(fontSize);
	}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        settings = PreferenceManager.getDefaultSharedPreferences(this);
        settings.registerOnSharedPreferenceChangeListener(new OnSharedPreferenceChangeListener() {
			@Override
			public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
				readPrefs();
			}
		});
        
        setContentView(R.layout.main);
        status = (TextView)findViewById(R.id.status);
        moveListScroll = (ScrollView)findViewById(R.id.scrollView);
        moveList = (TextView)findViewById(R.id.moveList);
        thinking = (TextView)findViewById(R.id.thinking);
		cb = (ChessBoard)findViewById(R.id.chessboard);
        ctrl = new ChessController(this);
        readPrefs();
        
        Typeface chessFont = Typeface.createFromAsset(getAssets(), "casefont.ttf");
        cb.setFont(chessFont);
        cb.setFocusable(true);
        cb.requestFocus();
        cb.setClickable(true);

        registerForContextMenu(status);
        registerForContextMenu(moveList);
        registerForContextMenu(thinking);

        ctrl.newGame(playerWhite, ttLogSize, false);
        if (savedInstanceState != null) {
        	String fen = savedInstanceState.getString("startFEN");
        	if (fen == null) {
        		fen = "";
        	}
        	String moves = savedInstanceState.getString("moves");
        	if (moves == null) {
        		moves = "";
        	}
        	String numUndo = savedInstanceState.getString("numUndo");
        	if (numUndo == null) {
        		numUndo = "0";
        	}
        	List<String> posHistStr = new ArrayList<String>();
        	posHistStr.add(fen);
        	posHistStr.add(moves);
        	posHistStr.add(numUndo);
        	ctrl.setPosHistory(posHistStr);
        }
        ctrl.startGame();
        
        cb.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
		        if (ctrl.humansTurn() && (event.getAction() == MotionEvent.ACTION_DOWN)) {
		            int sq = cb.eventToSquare(event);
		            Move m = cb.mousePressed(sq);
		            if (m != null) {
		                ctrl.humanMove(m);
		            }
		            return true;
		        }
		        return false;
			}
		});
        
        cb.setOnTrackballListener(new ChessBoard.OnTrackballListener() {
        	public void onTrackballEvent(MotionEvent event) {
		        if (ctrl.humansTurn()) {
		        	Move m = cb.handleTrackballEvent(event);
		        	if (m != null) {
		        		ctrl.humanMove(m);
		        	}
		        }
        	}
        });
    }

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		List<String> posHistStr = ctrl.getPosHistory();
		outState.putString("startFEN", posHistStr.get(0));
		outState.putString("moves", posHistStr.get(1));
		outState.putString("numUndo", posHistStr.get(2));
	}
	
	@Override
	protected void onDestroy() {
		ctrl.newGame(true, ttLogSize, false);
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.options_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.item_new_game:
	        ctrl.newGame(playerWhite, ttLogSize, false);
	        ctrl.startGame();
			return true;
		case R.id.item_quit:
			finish();
			return true;
		case R.id.item_undo:
			ctrl.takeBackMove();
			return true;
		case R.id.item_redo:
			ctrl.redoMove();
			return true;
		case R.id.item_settings:
		{
			Intent i = new Intent(CuckooChess.this, Preferences.class);
			startActivityForResult(i, 0);
			return true;
		}
		}
		return false;
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		getMenuInflater().inflate(R.menu.context_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
        if (!ctrl.humansTurn())
        	return false;
		switch (item.getItemId()) {
		case R.id.item_fen_to_clipboard: {
			String fen = ctrl.getFEN();
			ClipboardManager clipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
			clipboard.setText(fen);
			return true;
		}
		case R.id.item_clipboard_to_fen: {
			ClipboardManager clipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
			if (clipboard.hasText()) {
				String fen = clipboard.getText().toString();
				try {
					ctrl.setFEN(fen);
				} catch (ChessParseError e) {
					Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
				}
			}
			return true;
		}
		case R.id.item_pgn_to_clipboard: {
			String pgn = ctrl.getPGN();
			ClipboardManager clipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
			clipboard.setText(pgn);
			return true;
		}
		case R.id.item_clipboard_to_pgn: {
			return true;
		}
		}
		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0) {
			readPrefs();
		}
	}

	// FIXME!!! "Play white" should take effect directly after clipboard -> FEN,PGN or edit board.
	// FIXME!!! Implement "edit board" (And/or copy/paste FEN)
    
	@Override
	public void setPosition(Position pos) {
		cb.setPosition(pos);
	}

	@Override
	public void setSelection(int sq) {
		cb.setSelection(sq);
	}

	@Override
	public void setStatusString(String str) {
		status.setText(str);
	}

	@Override
	public void setMoveListString(String str) {
		moveList.setText(str);
		moveListScroll.fullScroll(ScrollView.FOCUS_DOWN);
	}
	
	@Override
	public void setThinkingString(String str) {
		thinking.setText(str);
	}

	@Override
	public int timeLimit() {
		return mTimeLimit;
	}

	@Override
	public boolean randomMode() {
		return mTimeLimit == -1;
	}

	@Override
	public boolean showThinking() {
		return mShowThinking;
	}

	static final int PROMOTE_DIALOG = 0; 
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case PROMOTE_DIALOG: {
			final CharSequence[] items = {"Queen", "Rook", "Bishop", "Knight"};
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Promote pawn to?");
			builder.setItems(items, new DialogInterface.OnClickListener() {
			    public void onClick(DialogInterface dialog, int item) {
	        		ctrl.reportPromotePiece(item);
			    }
			});
			AlertDialog alert = builder.create();
			return alert;
		}
		}
		return null;
	}

	@Override
	public void requestPromotePiece() {
		runOnUIThread(new Runnable() {
            public void run() {
            	showDialog(PROMOTE_DIALOG);
            }
		});
	}

	@Override
	public void runOnUIThread(Runnable runnable) {
		runOnUiThread(runnable);
	}
}