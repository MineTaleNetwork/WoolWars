package cc.minetale.woolwars;

import cc.minetale.slime.core.GameInfo;
import cc.minetale.slime.core.GameState;
import cc.minetale.slime.event.game.PostGameSetupEvent;
import cc.minetale.slime.event.game.PostGameStageChangeEvent;
import cc.minetale.slime.event.game.PreGameSetupEvent;
import cc.minetale.slime.event.team.GameSetupTeamsEvent;
import cc.minetale.slime.game.Game;
import cc.minetale.slime.game.GameManager;
import cc.minetale.slime.game.Stage;
import cc.minetale.slime.lobby.LobbyInstance;
import cc.minetale.slime.map.MapProvider;
import cc.minetale.slime.map.MapResolver;
import cc.minetale.slime.player.GamePlayer;
import cc.minetale.slime.player.PlayerState;
import cc.minetale.slime.rule.PlayerRule;
import cc.minetale.slime.spawn.GameSpawn;
import cc.minetale.slime.spawn.MapSpawn;
import cc.minetale.slime.team.*;
import cc.minetale.slime.utils.ApplyStrategy;
import cc.minetale.slime.utils.GameUtil;
import cc.minetale.slime.utils.Requirement;
import cc.minetale.slime.utils.TeamUtil;
import cc.minetale.slime.utils.sequence.Sequence;
import cc.minetale.slime.utils.sequence.SequenceBuilder;
import cc.minetale.woolwars.utils.MapUtil;
import cc.minetale.woolwars.vanilla.VanillaUtils;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.extensions.Extension;

import java.time.Duration;
import java.util.List;
import java.util.Set;

public class WoolWars extends Extension {

    @Getter private GameInfo info;
    @Getter private LobbyInstance lobbyInstance;

    @Override
    public LoadStatus initialize() {
        this.info = GameInfo.create("WoolWars", "woolwars")
                .setPlayerProvider(GamePlayer::new)
                .setTeamProvider(TeamProvider.DEFAULT)

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
        manager.setMinPlayers(2);
        manager.setMaxPlayers(2);
        manager.setMaxGames(8);

        manager.setTimelimit(Duration.ofMinutes(15));

        //Misc.
        this.lobbyInstance = GameUtil.createLobby(this.info);
        GameUtil.setPlayerProvider(this.info);

        //Events
        var global = MinecraftServer.getGlobalEventHandler();
        EventNode<Event> node = EventNode.all("woolWars");

        node.addListener(PreGameSetupEvent.class, event -> {
            var game = event.getGame();
            var instance = game.getMainInstance();

            List<MapSpawn> mapSpawns = event.getMapSpawns();
            List<GameSpawn> gameSpawns = GameUtil.simpleSpawnConversion(mapSpawns, instance);
            event.getGameSpawns().addAll(gameSpawns);
        });

        node.addListener(GameSetupTeamsEvent.class, event -> {
            List<GameTeam> teams = TeamUtil.createTeams(
                    this.info.getTeamProvider(), this.info.getTeamTypes(),
                    event.getGame(),
                    1, event.getPlayers().size());

            event.getTeams().addAll(teams);
            var assigner = TeamAssigner.simpleAssigner(1);
            event.setAssigner(assigner);
        });

        node.addListener(PostGameSetupEvent.class, event -> {
            var instance = event.getInstance();
            MapUtil.generateChests(instance, 0.01f);
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

        node.addChild(VanillaUtils.getDefaultEvents());

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
                    game.setRule(PlayerRule.FROZEN, PlayerRule.FreezeType.NONE, ApplyStrategy.ALWAYS, true);
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
