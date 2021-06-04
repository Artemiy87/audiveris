//------------------------------------------------------------------------------------------------//
//                                                                                                //
//                                    K e y A l t e r I n t e r                                   //
//                                                                                                //
//------------------------------------------------------------------------------------------------//
// <editor-fold defaultstate="collapsed" desc="hdr">
//
//  Copyright © Audiveris 2019. All rights reserved.
//
//  This program is free software: you can redistribute it and/or modify it under the terms of the
//  GNU Affero General Public License as published by the Free Software Foundation, either version
//  3 of the License, or (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
//  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
//  See the GNU Affero General Public License for more details.
//
//  You should have received a copy of the GNU Affero General Public License along with this
//  program.  If not, see <http://www.gnu.org/licenses/>.
//------------------------------------------------------------------------------------------------//
// </editor-fold>
package org.audiveris.omr.sig.inter;

import org.audiveris.omr.glyph.Glyph;
import org.audiveris.omr.glyph.Shape;
import org.audiveris.omr.math.PointUtil;
import org.audiveris.omr.sheet.Staff;
import static org.audiveris.omr.sig.inter.AlterInter.computePitch;
import org.audiveris.omr.sig.ui.InterEditor;
import org.audiveris.omr.sig.ui.InterEditor.Handle;

import java.awt.Point;
import java.awt.Rectangle;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Class {@code KeyAlterInter} is an Alteration inter, which is part of a key signature.
 *
 * @author Hervé Bitteur
 */
@XmlRootElement(name = "key-alter")
public class KeyAlterInter
        extends AlterInter
{

    /**
     * Creates a new KeyAlterInter object.
     *
     * @param glyph         underlying glyph
     * @param shape         precise shape
     * @param grade         evaluation value
     * @param staff         the related staff
     * @param pitch         the pitch value WRT staff
     * @param measuredPitch the measured pitch
     */
    public KeyAlterInter (Glyph glyph,
                          Shape shape,
                          double grade,
                          Staff staff,
                          double pitch,
                          double measuredPitch)
    {
        super(glyph, shape, grade, staff, pitch, measuredPitch);
    }

    /**
     * No-arg constructor needed for JAXB.
     */
    private KeyAlterInter ()
    {
        super((Glyph) null, null, 0, null, null, null);
    }

    //--------//
    // accept //
    //--------//
    @Override
    public void accept (InterVisitor visitor)
    {
        visitor.visit(this);
    }

    //-------//
    // added //
    //-------//
    @Override
    public void added ()
    {
        removed = false; // Skip AlterInter
    }

    //-----------//
    // getEditor //
    //-----------//
    @Override
    public InterEditor getEditor ()
    {
        return new Editor(this);
    }

    //---------------//
    // checkAbnormal //
    //---------------//
    @Override
    public boolean checkAbnormal ()
    {
        // Skip AlterInder
        return isAbnormal();
    }

    //--------//
    // create //
    //--------//
    /**
     * Create an Alter inter, with a grade value, determining pitch WRT provided staff.
     *
     * @param glyph underlying glyph
     * @param shape precise shape
     * @param grade evaluation value
     * @param staff related staff
     * @return the created instance or null if failed
     */
    public static KeyAlterInter create (Glyph glyph,
                                        Shape shape,
                                        double grade,
                                        Staff staff)
    {
        Pitches p = computePitch(glyph, shape, staff);

        return new KeyAlterInter(glyph, shape, grade, staff, p.pitch, p.measuredPitch);
    }

    //--------//
    // Editor //
    //--------//
    /**
     * User editor for a KeyAlter.
     * <p>
     * For a KeyAlter, we provide only one handle:
     * <ul>
     * <li>Middle handle, moving only horizontally
     * </ul>
     */
    private static class Editor
            extends InterEditor
    {

        // Original data
        private final Rectangle originalBounds;

        // Latest data
        private final Rectangle latestBounds;

        public Editor (final KeyAlterInter alter)
        {
            super(alter);

            originalBounds = alter.getBounds();
            latestBounds = alter.getBounds();

            // Middle handle: move horizontally only
            handles.add(selectedHandle = new Handle(alter.getCenter())
            {
                @Override
                public boolean applyMove (Point vector)
                {
                    final double dx = vector.getX();

                    if (dx == 0) {
                        return false;
                    }

                    // Data
                    latestBounds.x += dx;

                    // Handle
                    for (Handle handle : handles) {
                        PointUtil.add(handle.getHandleCenter(), dx, 0);
                    }

                    return true;
                }
            });
        }

        @Override
        protected void doit ()
        {
            inter.setBounds(latestBounds);

            super.doit(); // No more glyph
        }

        @Override
        public void undo ()
        {
            inter.setBounds(originalBounds);

            super.undo();
        }
    }
}
