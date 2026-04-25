MODEL=openai/gpt-oss-120b
DOMAIN=airline
uv run tau2 run \
  --agent llm_agent \
  --domain $DOMAIN \
  --agent-llm openrouter/$MODEL \
  --user-llm openrouter/$MODEL \
  --num-trials 10 --save-to gpt-oss-120b_"$DOMAIN" --max-concurrency 1
