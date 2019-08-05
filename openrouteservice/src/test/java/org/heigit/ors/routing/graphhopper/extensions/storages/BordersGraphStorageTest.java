/*  This file is part of Openrouteservice.
 *
 *  Openrouteservice is free software; you can redistribute it and/or modify it under the terms of the
 *  GNU Lesser General Public License as published by the Free Software Foundation; either version 2.1
 *  of the License, or (at your option) any later version.

 *  This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.

 *  You should have received a copy of the GNU Lesser General Public License along with this library;
 *  if not, see <https://www.gnu.org/licenses/>.
 */
package org.heigit.ors.routing.graphhopper.extensions.storages;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BordersGraphStorageTest {

    private final BordersGraphStorage _storage;

    public BordersGraphStorageTest() {
        _storage = new BordersGraphStorage();
        _storage.init();
        _storage.create(1);
    }

    @Test
    public void TestItemCreation() {
        _storage.setEdgeValue(1, (short)1, (short)2, (short)3);

        assertEquals(_storage.getEdgeValue(1, BordersGraphStorage.Property.TYPE), 1);
        assertEquals(_storage.getEdgeValue(1, BordersGraphStorage.Property.START), 2);
        assertEquals(_storage.getEdgeValue(1, BordersGraphStorage.Property.END), 3);
    }
}
