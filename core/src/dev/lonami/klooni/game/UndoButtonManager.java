/*
    1010! Klooni, a free customizable puzzle game for Android and Desktop
    Copyright (C) 2017-2019  Lonami Exo @ lonami.dev

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package dev.lonami.klooni.game;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import dev.lonami.klooni.actors.SoftButton;
import dev.lonami.klooni.serializer.BinSerializable;

// Manages the undo button state and behavior as part of the game state
public class UndoButtonManager implements BinSerializable {

    //region Members

    private final SoftButton undoButton;
    private boolean undoAvailable;
    private UndoActionHandler undoActionHandler;

    //endregion

    //region Constructor

    public UndoButtonManager(final GameLayout layout, final Stage stage) {
        // Create undo button exactly like replay button
        undoButton = new SoftButton(0, "replay_texture");
        undoButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                if (undoActionHandler != null && undoAvailable) {
                    undoActionHandler.performUndo();
                }
            }
        });

        // Position and add button to stage
        // Note: Using the basic update method since scorer is not available in constructor
        layout.update(undoButton);
        stage.addActor(undoButton);

        setUndoAvailable(false); // Initially not available
    }

    //endregion

    //region Public methods

    public void setUndoActionHandler(UndoActionHandler handler) {
        this.undoActionHandler = handler;
    }

    public void setUndoAvailable(boolean available) {
        this.undoAvailable = available;
        updateButtonState();
    }

    public void updateLayout(GameLayout layout) {
        layout.update(undoButton);
    }

    public void updateLayout(GameLayout layout, BaseScorer scorer) {
        layout.updateUndoButton(undoButton, scorer);
    }

    //endregion

    //region Private methods

    private void updateButtonState() {
        undoButton.setDisabled(!undoAvailable);
    }

    //endregion

    //region Serialization

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeBoolean(undoAvailable);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        setUndoAvailable(in.readBoolean());
    }

    //endregion

    //region Interfaces

    public interface UndoActionHandler {
        void performUndo();
    }

    //endregion
}
