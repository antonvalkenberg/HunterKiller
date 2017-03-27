package net.codepoke.ai.challenge.hunterkiller.orders;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
	 * @param structure
	 *            The structure this order is for.
	 * @param type
	 *            The type of order.
	 */
	public StructureOrder(Structure structure, StructureOrderType type) {
		super(structure);
		this.orderType = type;
	}

	// endregion

}
