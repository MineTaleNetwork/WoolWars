package cc.minetale.woolwars;

import cc.minetale.slime.core.GameInfo;
import cc.minetale.slime.core.GameState;
import cc.minetale.slime.core.TeamStyle;
import cc.minetale.slime.event.game.PostGameStageChangeEvent;
import cc.minetale.slime.event.game.PostInstanceSetupEvent;
import cc.minetale.slime.event.game.PreInstanceSetupEvent;
import cc.minetale.slime.event.team.GameSetupTeamsEvent;
import cc.minetale.slime.game.Game;
import cc.minetale.slime.game.GameManager;
import cc.minetale.slime.game.Stage;
import cc.minetale.slime.lobby.LobbyInstance;
import cc.minetale.slime.map.MapProvider;
import cc.minetale.slime.map.MapResolver;
import cc.minetale.slime.misc.Requirement;
import cc.minetale.slime.misc.sequence.Sequence;
import cc.minetale.slime.misc.sequence.SequenceBuilder;
import cc.minetale.slime.player.GamePlayer;
import cc.minetale.slime.player.PlayerState;
import cc.minetale.slime.spawn.GameSpawn;
import cc.minetale.slime.spawn.MapSpawn;
import cc.minetale.slime.team.ColorTeam;
import cc.minetale.slime.team.GameTeam;
import cc.minetale.slime.team.TeamAssigner;
import cc.minetale.slime.team.TeamProvider;
import cc.minetale.slime.utils.GameUtil;
import cc.minetale.slime.utils.TeamUtil;
import cc.minetale.woolwars.utils.MapUtil;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.extensions.Extension;
import net.minestom.server.utils.NamespaceID;
import net.minestom.server.world.DimensionType;
import net.minestom.server.world.DimensionTypeManager;

import java.time.Duration;
import java.util.List;
import java.util.Set;

public class WoolWars extends Extension {

    public static final DimensionTypeManager DIMENSION_TYPE_MANAGER = MinecraftServer.getDimensionTypeManager();

    @Getter private GameInfo info;
    @Getter private LobbyInstance lobbyInstance;

    private static final DimensionType fullBrightDimension = DimensionType
            .builder(NamespaceID.from("minestom:fullbright"))
            .ambientLight(2.0f)
            .build();

    @Override
    public LoadStatus initialize() {
        this.info = GameInfo.create("WoolWars", "woolwars")
                .setPlayerProvider(GamePlayer::new)
                .setTeamProvider(TeamProvider.DEFAULT)

                .setTeamStyle(TeamStyle.ANONYMOUS)
                .setTeamTypes(List.of(ColorTeam.values()))

                .setGameMapProvider(MapProvider.DEFAULT_GAME)
                .setGameMapResolver(MapResolver.DEFAULT_GAME)

                .setLobbyMapProvider(MapProvider.DEFAULT_LOBBY)
                .setLobbyMapResolver(MapResolver.DEFAULT_LOBBY)

                .setMapRequirements(Set.of(Requirement.Map.minSpawnsRequirement(1), Requirement.Map.ALL_SPAWNS_NOT_OWNED));

        //Don't continue if there was an issue or tooling is enabled
        if(!GameUtil.initialize(this.info)) { return LoadStatus.SUCCESS; }

        //Create GameManager
        var manager = GameManager.create(this.info, gameManager -> new WWGame(gameManager, new GameState()), null);
        manager.setMinPlayers(3);
        manager.setMaxPlayers(3);
        manager.setMaxGames(8);

        manager.setTimelimit(Duration.ofMinutes(15));

        //Misc.
        DIMENSION_TYPE_MANAGER.addDimension(fullBrightDimension);

        this.lobbyInstance = GameUtil.createLobby(this.info);

        //Events
        var global = MinecraftServer.getGlobalEventHandler();
        EventNode<Event> node = EventNode.all("woolWars");

        node.addListener(PreInstanceSetupEvent.class, event -> {
            event.setDimension(fullBrightDimension);
        });

        node.addListener(PostInstanceSetupEvent.class, event -> {
            var instance = event.getInstance();

            List<MapSpawn> mapSpawns = event.getMapSpawns();
            List<GameSpawn> gameSpawns = GameUtil.simpleSpawnConversion(mapSpawns, instance);
            event.getGameSpawns().addAll(gameSpawns);

            MapUtil.generateChests(instance, 0.01f);
        });

        node.addListener(GameSetupTeamsEvent.class, event -> {
            List<GameTeam> teams = TeamUtil.createAnonymousTeams(
                    this.info.getTeamProvider(),
                    event.getGame(),
                    1, event.getPlayers().size());

            event.getTeams().addAll(teams);
            var assigner = TeamAssigner.simpleAssigner(1); //TODO Configurable through private games
            event.setAssigner(assigner);
        });

        node.addListener(PostGameStageChangeEvent.class, event -> {
            var game = event.getGame();
            var stage = event.getNewStage();

            if(stage == Stage.PRE_GAME) {
                stagePreGame(game);
            } else if(stage == WWStage.GRACE_PERIOD) {
                stageGracePeriod(game);
            } else if(stage == Stage.IN_GAME) {
                stageInGame(game);
            }
        });

        global.addChild(node);

        return LoadStatus.SUCCESS;
    }

    private void stagePreGame(Game game) {
        game.setLoadout(Loadouts.DEFAULT);
        game.setState(WWPlayerState.STARTING);

        game.sendMessage(Component.text("Game starts in 1 second"));

        Sequence countdown = new SequenceBuilder(1000L)
                .animateExperienceBar(true)
                .onFinish(involved -> {
                    game.setState(PlayerState.PLAY);
                    game.getState().nextStage();
                })
                .build();

        countdown.addInvolved(game.getPlayers());
        countdown.start();
    }

    private void stageGracePeriod(Game game) {
        game.sendMessage(Component.text("Grace period ends in 1 second"));

        Sequence countdown = new SequenceBuilder(1000L)
                .animateExperienceBar(true)
                .onFinish(involved -> {
                    game.getState().nextStage();
                })
                .build();
        countdown.addInvolved(game.getPlayers());
        countdown.start();
    }

    private void stageInGame(Game game) {
        game.sendMessage(Component.text("Grace period has ended!"));
        if(game instanceof WWGame wwGame) {
            wwGame.startFloor();
        }
    }

    @Override
    public void terminate() { }

}
