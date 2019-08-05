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
package org.heigit.ors.util;

import java.util.Iterator;
import java.util.List;

public class ListUtility {
   public static <E> E getElement(List<E> list, E value)
   {
	    Iterator<E> iter = list.iterator();
	    while (iter.hasNext()) {
	        E c = iter.next();
	        if (c.equals(value)) {
	           return c;
	        }
	    }
	    
	    return null;
   }
}
