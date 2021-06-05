package com.windanesz.ancientspellcraft.constants;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public enum EnumFacingIntercardinal implements IStringSerializable {
	DOWN(0, 1, -1, "down", EnumFacingIntercardinal.AxisDirection.NEGATIVE, EnumFacingIntercardinal.Axis.Y, new Vec3i(0, -1, 0)),
	UP(1, 0, -1, "up", EnumFacingIntercardinal.AxisDirection.POSITIVE, EnumFacingIntercardinal.Axis.Y, new Vec3i(0, 1, 0)),
	NORTH(2, 3, 2, "north", EnumFacingIntercardinal.AxisDirection.NEGATIVE, EnumFacingIntercardinal.Axis.Z, new Vec3i(0, 0, -1)),
	SOUTH(3, 2, 0, "south", EnumFacingIntercardinal.AxisDirection.POSITIVE, EnumFacingIntercardinal.Axis.Z, new Vec3i(0, 0, 1)),
	WEST(4, 5, 1, "west", EnumFacingIntercardinal.AxisDirection.NEGATIVE, EnumFacingIntercardinal.Axis.X, new Vec3i(-1, 0, 0)),
	EAST(5, 4, 3, "east", EnumFacingIntercardinal.AxisDirection.POSITIVE, EnumFacingIntercardinal.Axis.X, new Vec3i(1, 0, 0)),

	NORTHEAST(6, 8, 4, "northeast", EnumFacingIntercardinal.AxisDirection.NEGATIVE, EnumFacingIntercardinal.Axis.X, new Vec3i(1, 0, -1)),
	SOUTHEAST(7, 9, 5, "southeast", EnumFacingIntercardinal.AxisDirection.NEGATIVE, EnumFacingIntercardinal.Axis.X, new Vec3i(1, 0, 1)),
	SOUTHWEST(8, 6, 6, "southwest", EnumFacingIntercardinal.AxisDirection.NEGATIVE, EnumFacingIntercardinal.Axis.X, new Vec3i(-1, 0, 1)),
	NORTHWEST(9, 7, 7, "northwest", EnumFacingIntercardinal.AxisDirection.NEGATIVE, EnumFacingIntercardinal.Axis.X, new Vec3i(-1, 0, -1));

	/**
	 * Ordering index for D-U-N-S-W-E-NE-SE-SW-NW
	 */
	private final int index;
	/**
	 * Index of the opposite Facing in the VALUES array
	 */
	private final int opposite;
	/**
	 * Ordering index for the HORIZONTALS field (S-W-N-E-NE-SE-SW-NW)
	 */
	private final int horizontalIndex;
	private final String name;
	private final EnumFacingIntercardinal.Axis axis;
	private final EnumFacingIntercardinal.AxisDirection axisDirection;
	/**
	 * Normalized Vector that points in the direction of this Facing
	 */
	private final Vec3i directionVec;
	/**
	 * All facings in D-U-N-S-W-E order
	 */
	public static final EnumFacingIntercardinal[] VALUES = new EnumFacingIntercardinal[10];
	/**
	 * All Facings with horizontal axis in order S-W-N-E
	 */
	public static final EnumFacingIntercardinal[] HORIZONTALS = new EnumFacingIntercardinal[8];
	private static final Map<String, EnumFacingIntercardinal> NAME_LOOKUP = Maps.<String, EnumFacingIntercardinal>newHashMap();

	private EnumFacingIntercardinal(int indexIn, int oppositeIn, int horizontalIndexIn, String nameIn, EnumFacingIntercardinal.AxisDirection axisDirectionIn, EnumFacingIntercardinal.Axis axisIn, Vec3i directionVecIn) {
		this.index = indexIn;
		this.horizontalIndex = horizontalIndexIn;
		this.opposite = oppositeIn;
		this.name = nameIn;
		this.axis = axisIn;
		this.axisDirection = axisDirectionIn;
		this.directionVec = directionVecIn;
	}

	/**
	 * Get the Index of this Facing (0-5). The order is D-U-N-S-W-E
	 */
	public int getIndex() {
		return this.index;
	}

	/**
	 * Get the index of this horizontal facing (0-3). The order is S-W-N-E
	 */
	public int getHorizontalIndex() {
		return this.horizontalIndex;
	}

	/**
	 * Get the AxisDirection of this Facing.
	 */
	public EnumFacingIntercardinal.AxisDirection getAxisDirection() {
		return this.axisDirection;
	}

	/**
	 * Get the opposite Facing (e.g. DOWN => UP)
	 */
	public EnumFacingIntercardinal getOpposite() {
		return byIndex(this.opposite);
	}

	/**
	 * Rotate this Facing around the given axis clockwise. If this facing cannot be rotated around the given axis,
	 * returns this facing without rotating.
	 */
	public EnumFacingIntercardinal rotateAround(EnumFacingIntercardinal.Axis axis) {
		switch (axis) {
			case X:

				if (this != WEST && this != EAST) {
					return this.rotateX();
				}

				return this;
			case Y:

				if (this != UP && this != DOWN) {
					return this.rotateY();
				}

				return this;
			case Z:

				if (this != NORTH && this != SOUTH) {
					return this.rotateZ();
				}

				return this;
			default:
				throw new IllegalStateException("Unable to get CW facing for axis " + axis);
		}
	}

	/**
	 * Rotate this Facing around the Y axis clockwise (NORTH => EAST => SOUTH => WEST => NORTH)
	 */
	public EnumFacingIntercardinal rotateY() {
		switch (this) {
			case NORTH:
				return EAST;
			case EAST:
				return SOUTH;
			case SOUTH:
				return WEST;
			case WEST:
				return NORTH;
			default:
				throw new IllegalStateException("Unable to get Y-rotated facing of " + this);
		}
	}

	/**
	 * Rotate this Facing around the X axis (NORTH => DOWN => SOUTH => UP => NORTH)
	 */
	private EnumFacingIntercardinal rotateX() {
		switch (this) {
			case NORTH:
				return DOWN;
			case EAST:
			case WEST:
			default:
				throw new IllegalStateException("Unable to get X-rotated facing of " + this);
			case SOUTH:
				return UP;
			case UP:
				return NORTH;
			case DOWN:
				return SOUTH;
		}
	}

	/**
	 * Rotate this Facing around the Z axis (EAST => DOWN => WEST => UP => EAST)
	 */
	private EnumFacingIntercardinal rotateZ() {
		switch (this) {
			case EAST:
				return DOWN;
			case SOUTH:
			default:
				throw new IllegalStateException("Unable to get Z-rotated facing of " + this);
			case WEST:
				return UP;
			case UP:
				return EAST;
			case DOWN:
				return WEST;
		}
	}

	/**
	 * Rotate this Facing around the Y axis counter-clockwise (NORTH => WEST => SOUTH => EAST => NORTH)
	 */
	public EnumFacingIntercardinal rotateYCCW() {
		switch (this) {
			case NORTH:
				return WEST;
			case EAST:
				return NORTH;
			case SOUTH:
				return EAST;
			case WEST:
				return SOUTH;
			default:
				throw new IllegalStateException("Unable to get CCW facing of " + this);
		}
	}

	/**
	 * Gets the offset in the x direction to the block in front of this facing.
	 */
	public int getXOffset() {
		switch (this) {
			case NORTHEAST:
				return 1;
			case SOUTHEAST:
				return 1;
			case SOUTHWEST:
				return -1;
			case NORTHWEST:
				return -1;
			default:
			return this.axis == EnumFacingIntercardinal.Axis.X ? this.axisDirection.getOffset() : 0;
		}
	}

	/**
	 * Gets the offset in the y direction to the block in front of this facing.
	 */
	public int getYOffset() {
		switch (this) {
			case NORTHEAST:
				return 0;
			case SOUTHEAST:
				return 0;
			case SOUTHWEST:
				return 0;
			case NORTHWEST:
				return 0;
			default:
				return this.axis == EnumFacingIntercardinal.Axis.Y ? this.axisDirection.getOffset() : 0;
		}
	}

	/**
	 * Gets the offset in the z direction to the block in front of this facing.
	 */
	public int getZOffset() {
		switch (this) {
			case NORTHEAST:
				return -1;
			case SOUTHEAST:
				return 1;
			case SOUTHWEST:
				return 1;
			case NORTHWEST:
				return -1;
			default:
				return this.axis == EnumFacingIntercardinal.Axis.Z ? this.axisDirection.getOffset() : 0;
		}
	}

	/**
	 * Same as getName, but does not override the method from Enum.
	 */
	public String getName2() {
		return this.name;
	}

	public EnumFacingIntercardinal.Axis getAxis() {
		return this.axis;
	}

	/**
	 * Get the facing specified by the given name
	 */
	@Nullable
	public static EnumFacingIntercardinal byName(String name) {
		return name == null ? null : (EnumFacingIntercardinal) NAME_LOOKUP.get(name.toLowerCase(Locale.ROOT));
	}

	/**
	 * Gets the EnumFacing corresponding to the given index (0-5). Out of bounds values are wrapped around. The order is
	 * D-U-N-S-W-E.
	 */
	public static EnumFacingIntercardinal byIndex(int index) {
		return VALUES[MathHelper.abs(index % VALUES.length)];
	}

	/**
	 * Gets the EnumFacing corresponding to the given horizontal index (0-3). Out of bounds values are wrapped around.
	 * The order is S-W-N-E.
	 */
	public static EnumFacingIntercardinal byHorizontalIndex(int horizontalIndexIn) {
		return HORIZONTALS[MathHelper.abs(horizontalIndexIn % HORIZONTALS.length)];
	}

	/**
	 * Get the EnumFacing corresponding to the given angle in degrees (0-360). Out of bounds values are wrapped around.
	 * An angle of 0 is SOUTH, an angle of 90 would be WEST.
	 */
	public static EnumFacingIntercardinal fromAngle(double angle) {
		return byHorizontalIndex(MathHelper.floor(angle / 90.0D + 0.5D) & 3);
	}

	/**
	 * Gets the angle in degrees corresponding to this EnumFacing.
	 */
	public float getHorizontalAngle() {
		return (float) ((this.horizontalIndex & 3) * 90);
	}

	/**
	 * Choose a random Facing using the given Random
	 */
	public static EnumFacingIntercardinal random(Random rand) {
		return values()[rand.nextInt(values().length)];
	}

	public static EnumFacingIntercardinal getFacingFromVector(float x, float y, float z) {
		EnumFacingIntercardinal enumfacing = NORTH;
		float f = Float.MIN_VALUE;

		for (EnumFacingIntercardinal enumfacing1 : values()) {
			float f1 = x * (float) enumfacing1.directionVec.getX() + y * (float) enumfacing1.directionVec.getY() + z * (float) enumfacing1.directionVec.getZ();

			if (f1 > f) {
				f = f1;
				enumfacing = enumfacing1;
			}
		}

		return enumfacing;
	}

	public String toString() {
		return this.name;
	}

	public String getName() {
		return this.name;
	}

	public static EnumFacingIntercardinal getFacingFromAxis(EnumFacingIntercardinal.AxisDirection axisDirectionIn, EnumFacingIntercardinal.Axis axisIn) {
		for (EnumFacingIntercardinal enumfacing : values()) {
			if (enumfacing.getAxisDirection() == axisDirectionIn && enumfacing.getAxis() == axisIn) {
				return enumfacing;
			}
		}

		throw new IllegalArgumentException("No such direction: " + axisDirectionIn + " " + axisIn);
	}

	/**
	 * Get a normalized Vector that points in the direction of this Facing.
	 */
	public Vec3i getDirectionVec() {
		return this.directionVec;
	}

	static {
		for (EnumFacingIntercardinal enumfacing : values()) {
			VALUES[enumfacing.index] = enumfacing;

			if (enumfacing.getAxis().isHorizontal()) {
				HORIZONTALS[enumfacing.horizontalIndex] = enumfacing;
			}

			NAME_LOOKUP.put(enumfacing.getName2().toLowerCase(Locale.ROOT), enumfacing);
		}
	}

	public static enum Axis implements Predicate<EnumFacingIntercardinal>, IStringSerializable {
		X("x", EnumFacingIntercardinal.Plane.HORIZONTAL),
		Y("y", EnumFacingIntercardinal.Plane.VERTICAL),
		Z("z", EnumFacingIntercardinal.Plane.HORIZONTAL);

		private static final Map<String, EnumFacingIntercardinal.Axis> NAME_LOOKUP = Maps.<String, EnumFacingIntercardinal.Axis>newHashMap();
		private final String name;
		private final EnumFacingIntercardinal.Plane plane;

		private Axis(String name, EnumFacingIntercardinal.Plane plane) {
			this.name = name;
			this.plane = plane;
		}

		/**
		 * Get the axis specified by the given name
		 */
		@Nullable
		public static EnumFacingIntercardinal.Axis byName(String name) {
			return name == null ? null : (EnumFacingIntercardinal.Axis) NAME_LOOKUP.get(name.toLowerCase(Locale.ROOT));
		}

		/**
		 * Like getName but doesn't override the method from Enum.
		 */
		public String getName2() {
			return this.name;
		}

		/**
		 * If this Axis is on the vertical plane (Only true for Y)
		 */
		public boolean isVertical() {
			return this.plane == EnumFacingIntercardinal.Plane.VERTICAL;
		}

		/**
		 * If this Axis is on the horizontal plane (true for X and Z)
		 */
		public boolean isHorizontal() {
			return this.plane == EnumFacingIntercardinal.Plane.HORIZONTAL;
		}

		public String toString() {
			return this.name;
		}

		public boolean apply(@Nullable EnumFacingIntercardinal p_apply_1_) {
			return p_apply_1_ != null && p_apply_1_.getAxis() == this;
		}

		/**
		 * Get this Axis' Plane (VERTICAL for Y, HORIZONTAL for X and Z)
		 */
		public EnumFacingIntercardinal.Plane getPlane() {
			return this.plane;
		}

		public String getName() {
			return this.name;
		}

		static {
			for (EnumFacingIntercardinal.Axis enumfacing$axis : values()) {
				NAME_LOOKUP.put(enumfacing$axis.getName2().toLowerCase(Locale.ROOT), enumfacing$axis);
			}
		}
	}

	public static enum AxisDirection {
		POSITIVE(1, "Towards positive"),
		NEGATIVE(-1, "Towards negative");

		private final int offset;
		private final String description;

		private AxisDirection(int offset, String description) {
			this.offset = offset;
			this.description = description;
		}

		/**
		 * Get the offset for this AxisDirection. 1 for POSITIVE, -1 for NEGATIVE
		 */
		public int getOffset() {
			return this.offset;
		}

		public String toString() {
			return this.description;
		}
	}

	public static enum Plane implements Predicate<EnumFacingIntercardinal>, Iterable<EnumFacingIntercardinal> {
		HORIZONTAL,
		VERTICAL;

		/**
		 * All EnumFacing values for this Plane
		 */
		public EnumFacingIntercardinal[] facings() {
			switch (this) {
				case HORIZONTAL:
					return new EnumFacingIntercardinal[] {EnumFacingIntercardinal.NORTH,
							EnumFacingIntercardinal.EAST,
							EnumFacingIntercardinal.SOUTH,
							EnumFacingIntercardinal.WEST};
				case VERTICAL:
					return new EnumFacingIntercardinal[] {EnumFacingIntercardinal.UP, EnumFacingIntercardinal.DOWN};
				default:
					throw new Error("Someone's been tampering with the universe!");
			}
		}

		/**
		 * Choose a random Facing from this Plane using the given Random
		 */
		public EnumFacingIntercardinal random(Random rand) {
			EnumFacingIntercardinal[] aenumfacing = this.facings();
			return aenumfacing[rand.nextInt(aenumfacing.length)];
		}

		public boolean apply(@Nullable EnumFacingIntercardinal p_apply_1_) {
			return p_apply_1_ != null && p_apply_1_.getAxis().getPlane() == this;
		}

		public Iterator<EnumFacingIntercardinal> iterator() {
			return Iterators.<EnumFacingIntercardinal>forArray(this.facings());
		}
	}
}