package de.viktorreiser.toolbox.widget;
//import de.viktorreiser.toolbox.widget.SwipeableListItem;

/**
 * Swipeable list item view.<br>
 * <br>
 * A view which want to use the technique described by {@link SwipeableListView} should implement
 * this interface and follow the rules descried here.<br>
 * <br>
 * The whole process of swipe is controlled by {@link #onViewSwipe(ListView, SwipeEvent, int, int)}
 * and the given {@link SwipeEvent}. {@link #swipeOnClick()}, {@link #swipeOnLongClick()} and
 * {@link #swipeDoesntHideListSelector()} return some extra settings.<br>
 * {@link #swipeStateReset()} is there for a clean handling.<br>
 * <br>
 * See {@link SwipeEvent} for a description of events and how to handle them.<br>
 * <br>
 * Further there is {@link SwipeableSetup} as helper for developing swipeable views.<br>
 * <br>
 * <b>Note</b>: This view itself can only listen for touch events in range of
 * {@link android.view.ViewConfiguration#getTouchSlop()}. Vertical move will be intercepted for list
 * scroll and horizontal move will be intercepted for swipe. So in the end you can listen for clicks
 * only since move action will e canceled after a certain offset.
 *
 * @author Viktor Reiser &lt;<a href="mailto:viktorreiser@gmx.de">viktorreiser@gmx.de</a>&gt;
 */
public abstract class SwipeableSetup {

    // PRIVATE ----------------------------------------------------------------------------

    /** Flag which locks setup for changes so {@link #checkChangesLock()} will throw. */
    protected boolean lockChanges = false;

    /** {@link #setStartOffset(int)} as absolute value (default {@code 50}). */
    protected int startOffset = 50;

    /** {@link #setStopOffset(int)} as absolute value (default {@code 100}). */
    protected int stopOffset = 100;

    /** {@link #setAnimationSpeed(int)} always {@code >= 0} (default {@code 500}). */
    protected int animationSpeed = 500;

    /** {@link #setStickyStart(boolean)} (default {@code true}). */
    protected boolean stickyStart = true;

    /** {@link #setSwipeOnClick(boolean)} (default {@code false}). */
    protected boolean consumeClick = false;

    /** {@link #setSwipeOnLongClick(boolean)} (default {@code false}) */
    protected boolean consumeLongClick = false;

    /** {@link #setHideSelectorOnStart(boolean)} but <b>inverted</b> (default {@code false}). */
    protected boolean dontHideSelector = false;

    /** {@link #setDetachFromList(boolean)} (default {@code false}) */
    protected boolean detachedFromList = false;

    // PUBLIC -----------------------------------------------------------------------------

    /**
     * Set offset in pixel until the swipe starts.<br>
     * <br>
     * Offset until SwipeableListItem#onViewSwipe(ListView, SwipeEvent, int, int) returns
     * {@code true}.<br>
     * <br>
     * <i>Is locked after the setup is attached to a swipeable view.</i>
     *
     * @param startOffset
     *            offset in pixel until the swipe starts
     */
    public void setStartOffset(int startOffset) {
        checkChangesLock();
        this.startOffset = Math.abs(startOffset);
    }

    /**
     * Set offset in pixel when an released (touch up) swipe should perform an action.<br>
     * <br>
     * This should be desirable greater than the given start offset but it doesn't have to. What
     * the action should be is the decision of the implementation of the swipeable view (it
     * could slide to the <i>end</i> when touch passed this stop offset).<br>
     * <br>
     * <i>Is locked after the setup is attached to a swipeable view.</i>
     *
     * @param stopOffset
     *            offset in pixel when an released (touch up) swipe should perform an action
     */
    public void setStopOffset(int stopOffset) {
        checkChangesLock();
        this.stopOffset = Math.abs(stopOffset);
    }

    /**
     * Should swipeable view jump the offset given with {@link #setStartOffset(int)} on start?
     *
     * @param stickyStart
     *            {@code true} when swipeable view should simulate a sticky start
     */
    public void setStickyStart(boolean stickyStart) {
        this.stickyStart = stickyStart;
    }

    /**
     * Should view perform swipe action on click?
     *
     * @param swipeOnClick
     *            {@code true} if view should perform swipe action on click
     *
     * @see SwipeableListItem#swipeOnClick()
     */
    public void setSwipeOnClick(boolean swipeOnClick) {
        consumeClick = swipeOnClick;
    }

    /**
     * Should view perform swipe action on long click?
     *
     * @param swipeOnLongClick
     *            {@code true} if view should perform swipe action on long click
     *
     * @see SwipeableListItem#swipeOnLongClick()
     */
    public void setSwipeOnLongClick(boolean swipeOnLongClick) {
        consumeLongClick = swipeOnLongClick;
    }

    /**
     * Should list hide selector when swipe of view starts?
     *
     * @param hideSelector
     *            {@code true} if selector should be hidden or {@code false} if it should remain
     *            visible on swipe
     *
     * @see SwipeableListItem#swipeDoesntHideListSelector()
     */
    public void setHideSelectorOnStart(boolean hideSelector) {
        dontHideSelector = !hideSelector;
    }

    /**
     * Set amount of millisecond which the swipeable needs to perform a complete animation.<br>
     * <br>
     * <i>Is locked after the setup is attached to a swipeable view.</i>
     *
     * @param animationSpeed
     *            amount of millisecond which the swipeable needs to perform a complete
     *            animation
     */
    public void setAnimationSpeed(int animationSpeed) {
        checkChangesLock();
        this.animationSpeed = animationSpeed < 0 ? 0 : animationSpeed;
    }

    /**
     * Is view detached from list?<br>
     * <br>
     * You can use this to use a swipeable view as stand-alone view which is not used in a
     * swipeable list view.
     *
     * @param detach
     *            {@code true} to detach swipeable view from list
     */
    public void setDetachFromList(boolean detach) {
        checkChangesLock();
        detachedFromList = detach;
    }

    // PRIVATE ----------------------------------------------------------------------------

    /**
     * Check whether setup object is locked for changes.<br>
     * <br>
     * This should be used in extending class in own configuration setters.<br>
     * {@link SwipeableListItem.SwipeableSetup} describes provides a sample pattern.
     */
    protected void checkChangesLock() {
        if (lockChanges) {
            throw new IllegalStateException(getClass().getSimpleName()
                    + " has been locked! Usually this happens if you already set the "
                    + " setup object and try to modify it after that.");
        }
    }
}
