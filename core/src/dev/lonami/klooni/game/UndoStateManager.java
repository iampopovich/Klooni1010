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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import dev.lonami.klooni.serializer.BinSerializable;

// Manages the game state for undo functionality
public class UndoStateManager implements BinSerializable {

    //region Members

    private byte[] savedState;
    private boolean hasUndoState;

    //endregion

    //region Constructor

    public UndoStateManager() {
        hasUndoState = false;
        savedState = null;
    }

    //endregion

    //region Public methods

    public void saveState(BinSerializable gameState) {
        try {
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            gameState.write(new DataOutputStream(out));
            savedState = out.toByteArray();
            hasUndoState = true;
        } catch (IOException e) {
            // This should not happen
            hasUndoState = false;
            savedState = null;
        }
    }

    public boolean restoreState(BinSerializable gameState) {
        if (!hasUndoState || savedState == null) {
            return false;
        }

        try {
            gameState.read(new DataInputStream(new ByteArrayInputStream(savedState)));
            hasUndoState = false;
            savedState = null;
            return true;
        } catch (IOException e) {
            // This should not happen
            hasUndoState = false;
            savedState = null;
            return false;
        }
    }

    public boolean hasUndoState() {
        return hasUndoState;
    }

    public void clearState() {
        hasUndoState = false;
        savedState = null;
    }

    //endregion

    //region Serialization

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeBoolean(hasUndoState);
        if (hasUndoState) {
            out.writeInt(savedState.length);
            out.write(savedState);
        }
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        hasUndoState = in.readBoolean();
        if (hasUndoState) {
            int length = in.readInt();
            savedState = new byte[length];
            in.readFully(savedState);
        } else {
            savedState = null;
        }
    }

    //endregion
}
