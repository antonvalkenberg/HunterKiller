package net.codepoke.ai.challenge.hunterkiller.orders;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.codepoke.ai.challenge.hunterkiller.HunterKillerAction;
import net.codepoke.ai.challenge.hunterkiller.HunterKillerConstants;
import net.codepoke.ai.challenge.hunterkiller.enums.StructureOrderType;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.mapfeature.Structure;
import net.codepoke.ai.challenge.hunterkiller.gameobjects.unit.Unit;

/**
 * Class representing a {@link HunterKillerOrder} performed by a {@link Structure}. Currently, structures
 * can only be ordered to spawn {@link Unit}s.
 * 
 * @author Anton Valkenberg (anton.valkenberg@gmail.com)
 *
 */
@Getter
@Setter
@NoArgsConstructor
public class StructureOrder
		extends HunterKillerOrder {

	// region Properties

	/**
	 * The type of order.
	 */
	private StructureOrderType orderType;

	// endregion

	// region Constructor

	/**
	 * Constructs a new instance.
	 * 
	 * {@link StructureOrder#StructureOrder(Structure, StructureOrderType, int)}
	 */
	public StructureOrder(Structure base, StructureOrderType type) {
		super(base, HunterKillerConstants.MOVEGENERATOR_DEFAULT_ACTION_INDEX);
		this.orderType = type;
	}

	/**
	 * Constructs a new instance.
	 * 
	 * @param structure
	 *            The structure this order is for.
	 * @param type
	 *            The type of order.
	 * @param actionIndex
	 *            The index this order has in the {@link HunterKillerAction}.
	 */
	public StructureOrder(Structure structure, StructureOrderType type, int actionIndex) {
		super(structure, actionIndex);
		this.orderType = type;
	}

	// endregion

}
